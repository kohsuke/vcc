package net.java.dev.vcc.impl.vmware.esx;

import com.vmware.vim25.RuntimeFaultFaultMsg;
import net.java.dev.vcc.api.Command;
import net.java.dev.vcc.api.Computer;
import net.java.dev.vcc.api.Host;
import net.java.dev.vcc.api.PowerState;
import net.java.dev.vcc.api.profiles.BasicProfile;
import net.java.dev.vcc.spi.AbstractDatacenter;
import net.java.dev.vcc.util.CompletedFuture;
import net.java.dev.vcc.util.TaskController;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * A VMware ESX Datacenter.
 */
public class ViDatacenter extends AbstractDatacenter {

    private final TaskController taskController = new ViTaskController();
    private final Object connectionLock = new Object();
    private ViConnection connection;
    private ExecutorService connectionExecutor = Executors.newCachedThreadPool(new ViThreadFactory());
    private final Set<ViHost> hosts = Collections.synchronizedSet(new HashSet<ViHost>());

    ViDatacenter(ViDatacenterId id, ViConnection connection) {
        super(id, BasicProfile.getInstance()); // TODO get capabilities
        this.connection = connection;
        connectionExecutor.submit(new Runnable() {
            public void run() {
                //ViDatacenter.this.connection.
            }
        });
    }

    public Set<Class<? extends Command>> getCommands() {
        return Collections.emptySet(); // TODO get commands
    }

    public <T extends Command> T execute(T command) {
        command.setSubmitted(new CompletedFuture("Unsupported command", new UnsupportedOperationException()));
        return command;
    }

    public Set<Host> getHosts() {
        return Collections.unmodifiableSet(new HashSet<Host>(hosts));
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
        synchronized (connectionLock) {
            try {
                connectionExecutor.shutdown();
                connection.getProxy().logout(connection.getSessionManager());
            } catch (RuntimeFaultFaultMsg e) {
                e.printStackTrace();  // TODO logging
            } finally {
                connection = null;
                connectionExecutor.shutdownNow();
            }
        }
    }

    public boolean isOpen() {
        synchronized (connectionLock) {
            return connection != null;
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
            return isOpen();
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
}
