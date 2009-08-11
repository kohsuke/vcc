package net.java.dev.vcc.impl.vmware.esx;

import com.vmware.vim25.InvalidPropertyFaultMsg;
import com.vmware.vim25.InvalidStateFaultMsg;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.TaskEvent;
import com.vmware.vim25.TaskInfo;
import com.vmware.vim25.TraversalSpec;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.VirtualMachineSnapshotInfo;
import net.java.dev.vcc.api.Command;
import net.java.dev.vcc.api.Computer;
import net.java.dev.vcc.api.DatacenterResourceGroup;
import net.java.dev.vcc.api.Host;
import net.java.dev.vcc.api.LogFactory;
import net.java.dev.vcc.api.PowerState;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A VMware ESX Datacenter.
 */
final class ViDatacenter
        extends AbstractDatacenter {

    private final TaskController taskController = new ViTaskController();

    private final Lock connectionLock = new ReentrantLock();

    private boolean connectionClosing = false;

    private final Condition closingCondition = connectionLock.newCondition();

    private final Condition closedCondition = connectionLock.newCondition();

    private ViConnection connection;

    private final ExecutorService connectionExecutor;

    private final Map<ViHostId, ViHost> hosts = Collections.synchronizedMap(new HashMap<ViHostId, ViHost>());

    private final Map<ViDatacenterResourceGroupId, ViDatacenterResourceGroup> resourceGroups =
            Collections.synchronizedMap(new HashMap<ViDatacenterResourceGroupId, ViDatacenterResourceGroup>());

    private final Map<String, AbstractManagedObject> model =
            Collections.synchronizedMap(new HashMap<String, AbstractManagedObject>());

    private final ViEventCollector eventCollector;

    private ManagedObjectReference rootFolder;
    private final ViEventDispatcher eventDispatcher;
    private final ViTaskCollector taskCollector;

    private ConcurrentMap<String, ViTaskContinuation<?>> pendingTasks =
            new ConcurrentHashMap<String, ViTaskContinuation<?>>();


    ViDatacenter(ViDatacenterId id, ViConnection connection, LogFactory logFactory, ExecutorService executorService)
            throws RuntimeFaultFaultMsg, InvalidStateFaultMsg, InvalidPropertyFaultMsg {
        super(logFactory, id, BasicProfile.getInstance()); // TODO get capabilities
        this.connection = connection;
        this.connectionExecutor = executorService;

        getLog().debug("Starting event collector");

        // 1. start collecting events
        eventDispatcher = new ViEventDispatcher(this, logFactory);
        eventCollector = new ViEventCollector(this, logFactory);
        taskCollector = new ViTaskCollector(this, logFactory);
        connectionExecutor.submit(new DefaultPollingTask(taskController, eventCollector, 1, TimeUnit.SECONDS));
        try {
            getLog().debug("Getting datacenter inventory");

            // 2. find what's out there
            TraversalSpec folderTraversalSpec =
                    Helper.newTraversalSpec("folderTraversalSpec", "Folder", "childEntity", false,
                            Helper.newSelectionSpec("folderTraversalSpec"),
                            Helper.newTraversalSpec("datacenterHostTraversalSpec", "Datacenter", "hostFolder",
                                    false, Helper.newSelectionSpec("folderTraversalSpec")),
                            Helper.newTraversalSpec("datacenterVmTraversalSpec", "Datacenter", "vmFolder",
                                    false, Helper.newSelectionSpec("folderTraversalSpec")),
                            Helper.newTraversalSpec("computeResourceRpTraversalSpec", "ComputeResource",
                                    "resourcePool", false,
                                    Helper.newSelectionSpec("resourcePoolTraversalSpec")),
                            Helper.newTraversalSpec("computeResourceHostTraversalSpec", "ComputeResource",
                                    "host", false),
                            Helper.newTraversalSpec("resourcePoolTraversalSpec", "ResourcePool",
                                    "resourcePool", false, Helper.newSelectionSpec(
                                            "resourcePoolTraversalSpec")));

            rootFolder = connection.getServiceContent().getRootFolder();
            PropertyFilterSpec spec = Helper.newPropertyFilterSpec(
                    new PropertySpec[]{
                            Helper.newPropertySpec("ManagedEntity", false, "name"),
                            Helper.newPropertySpec("ManagedEntity", false, "parent"),
                            Helper.newPropertySpec("VirtualMachine", false, "resourcePool"),
                            Helper.newPropertySpec("VirtualMachine", false, "config"),
                            Helper.newPropertySpec("VirtualMachine", false, "runtime"),
                            Helper.newPropertySpec("VirtualMachine", false, "snapshot"),
                    },
                    new ObjectSpec[]{Helper.newObjectSpec(rootFolder, false, folderTraversalSpec)});

            Map<String, AbstractManagedObject> model = new HashMap<String, AbstractManagedObject>();
            model.put(rootFolder.getValue(), this);
            Map<String, Collection<AbstractManagedObject>> waiting =
                    new HashMap<String, Collection<AbstractManagedObject>>();
            Map<String, String> proxyParents = new HashMap<String, String>();
            List<ObjectContent> entities =
                    connection.getProxy().retrieveProperties(connection.getServiceContent().getPropertyCollector(),
                            Collections.singletonList(spec));

            getLog().debug("Building model from inventory");

            for (ObjectContent entity : entities) {
                ManagedObjectReference entityObject = entity.getObj();
                if (model.containsKey(entityObject.getValue())) {
                    continue;
                }
                AbstractManagedObject entityMO;
                String entityType = entityObject.getType();
                String entityName = (String) Helper.getDynamicProperty(entity, "name");
                if ("VirtualMachine".equals(entityType)) {
                    VirtualMachineConfigInfo config = (VirtualMachineConfigInfo) Helper
                            .getDynamicProperty(entity, "config");
                    VirtualMachineRuntimeInfo runtime = (VirtualMachineRuntimeInfo) Helper
                            .getDynamicProperty(entity, "runtime");
                    VirtualMachineSnapshotInfo snapshot = (VirtualMachineSnapshotInfo) Helper
                            .getDynamicProperty(entity, "snapshot");
                    if (config != null && config.isTemplate()) {
                        entityMO = new ViComputerTemplate(this, new ViComputerTemplateId(getId(), entityObject), null,
                                entityName, config, runtime, snapshot);
                    } else {
                        entityMO = new ViComputer(this, new ViComputerId(getId(), entityObject), null, entityName,
                                config, runtime, snapshot);
                    }
                } else if ("ComputeResource".equals(entityType)) {
                    entityMO = new ViHost(this, new ViHostId(getId(), entityObject), null, entityName);
                } else if ("ResourcePool".equals(entityType)) {
                    entityMO = new ViHostResourceGroup(this, new ViHostResourceGroupId(getId(), entityObject), null,
                            entityName);
                } else if ("Folder".equals(entityType)) {
                    ManagedObjectReference parent = (ManagedObjectReference) Helper
                            .getDynamicProperty(entity, "parent");
                    if (parent != null && "Datacenter".equals(parent.getType())) {
                        proxyParents.put(entityObject.getValue(), parent.getValue());
                        continue;
                    }
                    entityMO = new ViDatacenterResourceGroup(this,
                            new ViDatacenterResourceGroupId(getId(), entityObject),
                            null, entityName);
                } else if ("Datacenter".equals(entityType)) {
                    entityMO = new ViDatacenterResourceGroup(this,
                            new ViDatacenterResourceGroupId(getId(), entityObject),
                            null, entityName);
                } else {
                    // unknown object type
                    continue;
                }
                ManagedObjectReference parent =
                        (ManagedObjectReference) Helper.getDynamicProperty(entity, "resourcePool");
                if (parent == null) {
                    parent = (ManagedObjectReference) Helper.getDynamicProperty(entity, "parent");
                }
                if (parent != null) {
                    AbstractManagedObject parentMO;
                    if (proxyParents.containsKey(parent.getValue())) {
                        parentMO = model.get(proxyParents.get(parent.getValue()));
                    } else {
                        parentMO = model.get(parent.getValue());
                    }
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
            this.model.putAll(model);
            for (Iterator<Map.Entry<String, Collection<AbstractManagedObject>>> it = waiting.entrySet().iterator();
                 it.hasNext();) {
                Map.Entry<String, Collection<AbstractManagedObject>> waitingMOs = it.next();
                if (proxyParents.containsKey(waitingMOs.getKey())) {
                    AbstractManagedObject parentMO;
                    if (proxyParents.containsKey(waitingMOs.getKey())) {
                        parentMO = model.get(proxyParents.get(waitingMOs.getKey()));
                        for (AbstractManagedObject childMO : waitingMOs.getValue()) {
                            addChildMO(parentMO, childMO);
                        }
                    }
                    it.remove();
                }
            }

            if (waiting.isEmpty()) {
                getLog().debug("Datacenter model constructed successfully");
            } else {
                getLog().warn("Datacenter model is not complete: "
                        + "{0} parents were referenced from the inventory but not provided in the inventory",
                        waiting.size());
            }
            // 3. start dispatching events

            getLog().debug("Starting event dispatcher");
            connectionExecutor.submit(eventDispatcher);
            connectionExecutor.submit(new DefaultPollingTask(taskController, taskCollector, 1, TimeUnit.SECONDS));
        } catch (RuntimeException e) {
            close();
            throw e;
        } catch (RuntimeFaultFaultMsg e) {
            close();
            throw e;
        } catch (InvalidPropertyFaultMsg e) {
            close();
            throw e;
        }
        getLog().debug("Datacenter created");
    }

    private void addChildMO(AbstractManagedObject parentMO, AbstractManagedObject childMO) {
        if (parentMO instanceof ViHostResourceGroup) {
            if (childMO instanceof ViComputer) {
                ((ViHostResourceGroup) parentMO).addComputer((ViComputer) childMO);
            } else if (childMO instanceof ViHostResourceGroup) {
                ((ViHostResourceGroup) parentMO).addHostResourceGroup((ViHostResourceGroup) childMO);
            }
        } else if (parentMO instanceof ViHost) {
            if (childMO instanceof ViComputer) {
                ((ViHost) parentMO).addComputer((ViComputer) childMO);
            } else if (childMO instanceof ViHostResourceGroup) {
                ((ViHost) parentMO).addHostResourceGroup((ViHostResourceGroup) childMO);
            }
        } else if (parentMO instanceof ViDatacenterResourceGroup) {
            if (childMO instanceof ViHost) {
                ((ViDatacenterResourceGroup) parentMO).addHost((ViHost) childMO);
            } else if (childMO instanceof ViDatacenterResourceGroup) {
                ((ViDatacenterResourceGroup) parentMO).addResourceGroup((ViDatacenterResourceGroup) childMO);
            } else if (childMO instanceof ViComputerTemplate) {
                ((ViDatacenterResourceGroup) parentMO).addComputerTemplate((ViComputerTemplate) childMO);
            }
        } else if (parentMO instanceof ViDatacenter) {
            if (childMO instanceof ViDatacenterResourceGroup) {
                ((ViDatacenter) parentMO).addDatacenterResourceGroup((ViDatacenterResourceGroup) childMO);
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

    public void addDatacenterResourceGroup(ViDatacenterResourceGroup viResourceGroup) {
        resourceGroups.put(viResourceGroup.getId(), viResourceGroup);
    }

    public void removeResourceGroup(ViDatacenterResourceGroup viResourceGroup) {
        resourceGroups.remove(viResourceGroup.getId());
    }

    public Set<Class<? extends Command>> getCommands() {
        return Collections.emptySet(); // TODO get commands
    }

    public <T extends Command> T execute(T command) {
        command.setSubmitted(new CompletedFuture("Unsupported command", new UnsupportedOperationException()));
        return command;
    }

    public String getName() {
        return getId().getDatacenterUrl();
    }

    public Set<Host> getHosts() {
        return Collections.unmodifiableSet(new HashSet<Host>(hosts.values()));
    }

    public Set<DatacenterResourceGroup> getDatacenterResourceGroups() {
        return Collections.unmodifiableSet(new HashSet<DatacenterResourceGroup>(resourceGroups.values()));
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
        }
        finally {
            connectionLock.unlock();
        }
        connectionLock.lock();
        try {
            try {
                connectionExecutor.shutdown();
                connection.getProxy().logout(connection.getSessionManager());
            }
            catch (RuntimeFaultFaultMsg e) {
                e.printStackTrace();  // TODO logging
            }
            finally {
                connection = null;
                connectionExecutor.shutdownNow();
            }
        }
        finally {
            closedCondition.signalAll();
            connectionLock.unlock();
        }
    }

    public boolean isOpen() {
        connectionLock.lock();
        try {
            return connection != null && !connectionClosing;
        }
        finally {
            connectionLock.unlock();
        }
    }

    public boolean isClosing() {
        connectionLock.lock();
        try {
            return connectionClosing;
        }
        finally {
            connectionLock.unlock();
        }
    }

    void awaitClosed()
            throws InterruptedException {
        connectionLock.lock();
        try {
            while (isOpen()) {
                closedCondition.await();
            }
        }
        finally {
            connectionLock.unlock();
        }
    }

    boolean awaitClosed(long timeout, TimeUnit unit)
            throws InterruptedException {
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
        }
        finally {
            connectionLock.unlock();
        }
    }

    void awaitClosing()
            throws InterruptedException {
        connectionLock.lock();
        try {
            while (!isClosing()) {
                closedCondition.await();
            }
        }
        finally {
            connectionLock.unlock();
        }
    }

    boolean awaitClosing(long timeout, TimeUnit unit)
            throws InterruptedException {
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
        }
        finally {
            connectionLock.unlock();
        }
    }

    ViConnection getConnection() {
        return connection;
    }

    public ViEventCollector getEventCollector() {
        return eventCollector;
    }

    public AbstractManagedObject getManagedObject(ManagedObjectReference value) {
        return model.get(value.getValue());
    }

    <T> Future<T> addPendingTask(ManagedObjectReference moRef, ViTaskContinuation<T> c) {
        pendingTasks.put(moRef.getValue(), c);
        return c.getFuture();
    }

    void processTask(TaskEvent taskEvent) {
        processTask(taskEvent.getInfo());
    }

    public void processTask(TaskInfo taskInfo) {
        ViTaskContinuation<?> continuation = pendingTasks.get(taskInfo.getTask().getValue());
        if (continuation != null) {
            switch (taskInfo.getState()) {
                case SUCCESS:
                    continuation.onSuccess();
                    pendingTasks.remove(taskInfo.getTask().getValue());
                    break;
                case ERROR:
                    continuation.onError(taskInfo.getError());
                    pendingTasks.remove(taskInfo.getTask().getValue());
                    break;
            }
        }
    }

    private static final class ResourceHolder {
        private static final Map<PowerState, Set<PowerState>> ALLOWED_TRANSITIONS;

        static {
            TreeMap<PowerState, Set<PowerState>> map = new TreeMap<PowerState, Set<PowerState>>();
            map.put(PowerState.STOPPED,
                    Collections.unmodifiableSet(new TreeSet<PowerState>(Arrays.asList(PowerState.RUNNING))));
            map.put(PowerState.SUSPENDED, Collections.unmodifiableSet(
                    new TreeSet<PowerState>(Arrays.asList(PowerState.STOPPED, PowerState.RUNNING))));
            map.put(PowerState.RUNNING, Collections.unmodifiableSet(
                    new TreeSet<PowerState>(Arrays.asList(PowerState.STOPPED, PowerState.SUSPENDED))));
            ALLOWED_TRANSITIONS = Collections.unmodifiableMap(map);
        }
    }

    private final class ViTaskController
            implements TaskController {

        /**
         * {@inheritDoc}
         */
        public boolean isActive() {
            return !isClosing();
        }

        /**
         * {@inheritDoc}
         */
        public void awaitDeactivated()
                throws InterruptedException {
            awaitClosing();
        }

        /**
         * {@inheritDoc}
         */
        public boolean awaitDeactivated(long timeout, TimeUnit unit)
                throws InterruptedException {
            return awaitClosing(timeout, unit);
        }
    }

    @Override
    public ViDatacenterId getId() {
        return (ViDatacenterId) super.getId();
    }
}
