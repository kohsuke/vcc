package net.java.dev.vcc.impl.vmware.esx;

import com.vmware.vim25.RuntimeFaultFaultMsg;
import net.java.dev.vcc.api.Command;
import net.java.dev.vcc.api.Computer;
import net.java.dev.vcc.api.Host;
import net.java.dev.vcc.api.PowerState;
import net.java.dev.vcc.api.profiles.BasicProfile;
import net.java.dev.vcc.spi.AbstractDatacenter;
import net.java.dev.vcc.util.CompletedFuture;

import java.util.*;

/**
 * A VMware ESX Datacenter.
 */
public class ViDatacenter extends AbstractDatacenter {

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

    private final Object connectionLock = new Object();
    private ViConnection connection;

    ViDatacenter(ViDatacenterId id, ViConnection connection) {
        super(id, BasicProfile.getInstance()); // TODO get capabilities
        this.connection = connection;
    }

    public Set<Class<? extends Command>> getCommands() {
        return Collections.emptySet(); // TODO get commands
    }

    public <T extends Command> T execute(T command) {
        command.setSubmitted(new CompletedFuture("Unsupported command", new UnsupportedOperationException()));
        return command;
    }

    public Set<Host> getHosts() {
        return Collections.emptySet(); // TODO get commands
    }

    public Set<Computer> getComputers() {
        return Collections.emptySet(); // TODO get commands
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
                connection.getProxy().logout(connection.getSessionManager());
            } catch (RuntimeFaultFaultMsg e) {
                e.printStackTrace();  // TODO logging
            } finally {
                connection = null;
            }
        }
    }

    public boolean isOpen() {
        synchronized (connectionLock) {
            return connection != null;
        }
    }
}
