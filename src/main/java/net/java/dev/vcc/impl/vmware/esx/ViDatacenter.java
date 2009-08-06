package net.java.dev.vcc.impl.vmware.esx;

import com.vmware.vim25.Event;
import com.vmware.vim25.EventFilterSpec;
import com.vmware.vim25.GeneralEvent;
import com.vmware.vim25.InvalidPropertyFaultMsg;
import com.vmware.vim25.InvalidStateFaultMsg;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.TraversalSpec;
import net.java.dev.vcc.api.Command;
import net.java.dev.vcc.api.Computer;
import net.java.dev.vcc.api.Host;
import net.java.dev.vcc.api.PowerState;
import net.java.dev.vcc.api.ResourceGroup;
import net.java.dev.vcc.api.profiles.BasicProfile;
import net.java.dev.vcc.impl.vmware.esx.vim25.Helper;
import net.java.dev.vcc.spi.AbstractDatacenter;
import net.java.dev.vcc.spi.AbstractManagedObject;
import net.java.dev.vcc.util.CompletedFuture;
import net.java.dev.vcc.util.DefaultPollingTask;
import net.java.dev.vcc.util.TaskController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A VMware ESX Datacenter.
 */
final class ViDatacenter extends AbstractDatacenter {

    private final TaskController taskController = new ViTaskController();
    private final Lock connectionLock = new ReentrantLock();
    private boolean connectionClosing = false;
    private final Condition closingCondition = connectionLock.newCondition();
    private final Condition closedCondition = connectionLock.newCondition();
    private ViConnection connection;
    private ExecutorService connectionExecutor = Executors.newCachedThreadPool(new ViThreadFactory());
    private final Map<ViHostId, ViHost> hosts = Collections.synchronizedMap(new HashMap<ViHostId, ViHost>());
    private final Map<ViResourceGroupId, ViResourceGroup> resourceGroups = Collections
            .synchronizedMap(new HashMap<ViResourceGroupId, ViResourceGroup>());
    private ViDatacenter.ViEventCollector eventCollector;
    private ManagedObjectReference rootFolder;

    ViDatacenter(ViDatacenterId id, ViConnection connection)
            throws RuntimeFaultFaultMsg, InvalidStateFaultMsg, InvalidPropertyFaultMsg {
        super(id, BasicProfile.getInstance()); // TODO get capabilities
        this.connection = connection;

        // 1. start collecting events
        eventCollector = new ViEventCollector();
        connectionExecutor.submit(new DefaultPollingTask(taskController, eventCollector, 1, TimeUnit.SECONDS));

        // 2. find what's out there
        TraversalSpec folderTraversalSpec = Helper
                .newTraversalSpec("folderTraversalSpec", "Folder", "childEntity", false,
                        Helper.newSelectionSpec("folderTraversalSpec"),
                        Helper.newTraversalSpec("datacenterHostTraversalSpec", "Datacenter", "hostFolder", false,
                                Helper.newSelectionSpec("folderTraversalSpec")),
                        Helper.newTraversalSpec("datacenterVmTraversalSpec", "Datacenter", "vmFolder", false,
                                Helper.newSelectionSpec("folderTraversalSpec")),
                        Helper.newTraversalSpec("computeResourceRpTraversalSpec", "ComputeResource", "resourcePool",
                                false,
                                Helper.newSelectionSpec("resourcePoolTraversalSpec")),
                        Helper.newTraversalSpec("computeResourceHostTraversalSpec", "ComputeResource", "host",
                                false),
                        Helper.newTraversalSpec("resourcePoolTraversalSpec", "ResourcePool", "resourcePool", false,
                                Helper.newSelectionSpec("resourcePoolTraversalSpec")));

        rootFolder = connection.getServiceContent().getRootFolder();
        PropertyFilterSpec spec = Helper.newPropertyFilterSpec(
                new PropertySpec[]{Helper.newPropertySpec("ManagedEntity", false, "name"),
                        Helper.newPropertySpec("ManagedEntity", false, "parent"),
                        Helper.newPropertySpec("VirtualMachine", false, "resourcePool"),
                },
                new ObjectSpec[]{Helper.newObjectSpec(rootFolder, false, folderTraversalSpec)});

        Map<String, AbstractManagedObject> model = new HashMap<String, AbstractManagedObject>();
        model.put(rootFolder.getValue(), this);
        Map<String, Collection<AbstractManagedObject>> waiting
                = new HashMap<String, Collection<AbstractManagedObject>>();
        List<ObjectContent> entities = connection.getProxy()
                .retrieveProperties(connection.getServiceContent().getPropertyCollector(),
                        Collections.singletonList(spec));
        for (ObjectContent entity : entities) {
            ManagedObjectReference entityObject = entity.getObj();
            if (model.containsKey(entityObject.getValue())) {
                continue;
            }
            AbstractManagedObject entityMO;
            String entityType = entityObject.getType();
            String entityName = (String) Helper.getDynamicProperty(entity, "name");
            if ("VirtualMachine".equals(entityType)) {
                entityMO = new ViComputer(this, new ViComputerId(getId(), entityObject), null, entityName);
            } else if ("ComputeResource".equals(entityType)) {
                entityMO = new ViHost(this, new ViHostId(getId(), entityObject), null, entityName);
            } else if ("ResourcePool".equals(entityType)) {
                entityMO = new ViResourceGroup(this, new ViResourceGroupId(getId(), entityObject), null, entityName);
            } else if ("Folder".equals(entityType)) {
                entityMO = new ViResourceGroup(this, new ViResourceGroupId(getId(), entityObject), null, entityName);
            } else if ("Datacenter".equals(entityType)) {
                entityMO = new ViResourceGroup(this, new ViResourceGroupId(getId(), entityObject), null, entityName);
            } else {
                // unknown object type
                continue;
            }
            ManagedObjectReference parent = (ManagedObjectReference) Helper.getDynamicProperty(entity, "resourcePool");
            if (parent == null) {
                parent = (ManagedObjectReference) Helper.getDynamicProperty(entity, "parent");
            }
            if (parent != null) {
                AbstractManagedObject parentMO = model.get(parent.getValue());
                if (parentMO != null) {
                    addChildMO(parentMO, entityMO);
                } else {
                    Collection<AbstractManagedObject> pendingChildMOs = waiting.get(parent.getValue());
                    if (pendingChildMOs == null) {
                        waiting.put(parent.getValue(), pendingChildMOs = new ArrayList<AbstractManagedObject>());
                    }
                    pendingChildMOs.add(entityMO);
                }
            }
            Collection<AbstractManagedObject> _waiting = waiting.get(entityObject.getValue());
            if (_waiting != null) {
                for (AbstractManagedObject childMO : _waiting) {
                    addChildMO(entityMO, childMO);
                }
                waiting.remove(entityObject.getValue());
            }
            model.put(entityObject.getValue(), entityMO);
        }
        // 3. start dispatching events
    }

    private void addChildMO(AbstractManagedObject parentMO, AbstractManagedObject childMO) {
        if (parentMO instanceof ViHost) {
            if (childMO instanceof ViComputer) {
                ((ViHost) parentMO).addComputer((ViComputer) childMO);
            } else if (childMO instanceof ViResourceGroup) {
                ((ViHost) parentMO).addResourceGroup((ViResourceGroup) childMO);
            }
        } else if (parentMO instanceof ViResourceGroup) {
            if (childMO instanceof ViComputer) {
                ((ViResourceGroup) parentMO).addComputer((ViComputer) childMO);
            } else if (childMO instanceof ViHost) {
                ((ViResourceGroup) parentMO).addHost((ViHost) childMO);
            } else if (childMO instanceof ViResourceGroup) {
                ((ViResourceGroup) parentMO).addResourceGroup((ViResourceGroup) childMO);
            }
        } else if (parentMO instanceof ViDatacenter) {
            if (childMO instanceof ViResourceGroup) {
                ((ViDatacenter) parentMO).addResourceGroup((ViResourceGroup) childMO);
            } else if (childMO instanceof ViHost) {
                ((ViDatacenter) parentMO).addHost((ViHost) childMO);
            }
        }
    }

    void addHost(ViHost viHost) {
        hosts.put(viHost.getId(), viHost);
    }

    void removeHost(ViHost viHost) {
        hosts.remove(viHost.getId());
    }

    public void addResourceGroup(ViResourceGroup viResourceGroup) {
        resourceGroups.put(viResourceGroup.getId(), viResourceGroup);
    }

    public void removeResourceGroup(ViResourceGroup viResourceGroup) {
        resourceGroups.remove(viResourceGroup.getId());
    }

    public Set<Class<? extends Command>> getCommands() {
        return Collections.emptySet(); // TODO get commands
    }

    public <T extends Command> T execute(T command) {
        command.setSubmitted(new CompletedFuture("Unsupported command", new UnsupportedOperationException()));
        return command;
    }

    public Set<Host> getHosts() {
        return Collections.unmodifiableSet(new HashSet<Host>(hosts.values()));
    }

    public Set<ResourceGroup> getResourceGroups() {
        return Collections.unmodifiableSet(new HashSet<ResourceGroup>(resourceGroups.values()));
    }

    public Set<Computer> getComputers() {
        Set<Computer> result = new HashSet<Computer>();
        for (Host host : getHosts()) {
            result.addAll(host.getComputers());
        }
        return Collections.unmodifiableSet(result);
    }

    public Set<PowerState> getAllowedStates() {
        return ResourceHolder.ALLOWED_TRANSITIONS.keySet();
    }

    public Set<PowerState> getAllowedStates(PowerState from) {
        Set<PowerState> states = ResourceHolder.ALLOWED_TRANSITIONS.get(from);
        if (states != null) {
            return states;
        }
        return Collections.emptySet();
    }

    public void close() {
        connectionLock.lock();
        try {
            if (connectionClosing) {
                return;
            }
            connectionClosing = true;
            closingCondition.signalAll();
        } finally {
            connectionLock.unlock();
        }
        connectionLock.lock();
        try {
            try {
                connectionExecutor.shutdown();
                connection.getProxy().logout(connection.getSessionManager());
            } catch (RuntimeFaultFaultMsg e) {
                e.printStackTrace();  // TODO logging
            } finally {
                connection = null;
                connectionExecutor.shutdownNow();
            }
        } finally {
            closedCondition.signalAll();
            connectionLock.unlock();
        }
    }

    public boolean isOpen() {
        connectionLock.lock();
        try {
            return connection != null && !connectionClosing;
        } finally {
            connectionLock.unlock();
        }
    }

    public boolean isClosing() {
        connectionLock.lock();
        try {
            return connectionClosing;
        } finally {
            connectionLock.unlock();
        }
    }

    void awaitClosed() throws InterruptedException {
        connectionLock.lock();
        try {
            while (isOpen()) {
                closedCondition.await();
            }
        } finally {
            connectionLock.unlock();
        }
    }

    boolean awaitClosed(long timeout, TimeUnit unit) throws InterruptedException {
        connectionLock.lock();
        try {
            long nanosTimeout = unit.toNanos(timeout);
            while (isOpen()) {
                if (nanosTimeout > 0) {
                    nanosTimeout = closedCondition.awaitNanos(nanosTimeout);
                } else {
                    return false;
                }
            }
            return true;
        } finally {
            connectionLock.unlock();
        }
    }

    void awaitClosing() throws InterruptedException {
        connectionLock.lock();
        try {
            while (!isClosing()) {
                closedCondition.await();
            }
        } finally {
            connectionLock.unlock();
        }
    }

    boolean awaitClosing(long timeout, TimeUnit unit) throws InterruptedException {
        connectionLock.lock();
        try {
            long nanosTimeout = unit.toNanos(timeout);
            while (!isClosing()) {
                if (nanosTimeout > 0) {
                    nanosTimeout = closedCondition.awaitNanos(nanosTimeout);
                } else {
                    return false;
                }
            }
            return true;
        } finally {
            connectionLock.unlock();
        }
    }

    private static final class ResourceHolder {
        private static final Map<PowerState, Set<PowerState>> ALLOWED_TRANSITIONS;

        static {
            TreeMap<PowerState, Set<PowerState>> map = new TreeMap<PowerState, Set<PowerState>>();
            map.put(PowerState.STOPPED, Collections.unmodifiableSet(new TreeSet<PowerState>(
                    Arrays.asList(PowerState.RUNNING))
            ));
            map.put(PowerState.SUSPENDED, Collections.unmodifiableSet(new TreeSet<PowerState>(
                    Arrays.asList(PowerState.STOPPED, PowerState.RUNNING))
            ));
            map.put(PowerState.RUNNING, Collections.unmodifiableSet(new TreeSet<PowerState>(
                    Arrays.asList(PowerState.STOPPED, PowerState.SUSPENDED))
            ));
            ALLOWED_TRANSITIONS = Collections.unmodifiableMap(map);
        }
    }

    private final class ViTaskController implements TaskController {

        /**
         * {@inheritDoc}
         */
        public boolean isActive() {
            return !isClosing();
        }

        /**
         * {@inheritDoc}
         */
        public void awaitDeactivated() throws InterruptedException {
            awaitClosing();
        }

        /**
         * {@inheritDoc}
         */
        public boolean awaitDeactivated(long timeout, TimeUnit unit) throws InterruptedException {
            return awaitClosing(timeout, unit);
        }
    }

    private final class ViEventCollector implements Runnable {

        private final ManagedObjectReference eventHistoryCollector;
        private final Queue<Event> events = new ConcurrentLinkedQueue<Event>();

        public ViEventCollector() throws RuntimeFaultFaultMsg, InvalidStateFaultMsg {
            this.eventHistoryCollector = connection.getProxy()
                    .createCollectorForEvents(connection.getServiceContent().getEventManager(), new EventFilterSpec());
            connection.getProxy().resetCollector(eventHistoryCollector);
        }

        public void run() {
            System.out.println("Starting collecting events " + new Date());
            boolean finished = false;
            while (!isClosing() && !finished) {
                List<Event> events = null;
                try {
                    events = connection.getProxy().readNextEvents(eventHistoryCollector, 100);
                } catch (RuntimeFaultFaultMsg e) {
                    log(e);
                    return;
                }
                if (events.isEmpty()) {
                    finished = true;
                } else {
                    this.events.addAll(events);
                }
            }
            if (isClosing() && finished) {
                // TODO handle the final event correctly
                this.events.add(new GeneralEvent());
            }
            System.out.println("Finished collecting events " + new Date() + events.size());
        }
    }

    private static class ViThreadFactory implements ThreadFactory {
        private final ThreadFactory delegate = Executors.defaultThreadFactory();

        public Thread newThread(Runnable r) {
            Thread result = delegate.newThread(r);
            result.setName("VMwareESX-" + result.getName());
            return result;
        }
    }

    void log(Throwable t) {
        // ignore for now;
    }

    @Override
    public ViDatacenterId getId() {
        return (ViDatacenterId) super.getId();
    }
}
