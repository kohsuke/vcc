package net.java.dev.vcc.impl.vmware.esx;

import com.vmware.vim25.RuntimeFaultFaultMsg;
import net.java.dev.vcc.api.Command;
import net.java.dev.vcc.api.Computer;
import net.java.dev.vcc.api.Host;
import net.java.dev.vcc.api.PowerState;
import net.java.dev.vcc.api.profiles.BasicProfile;
import net.java.dev.vcc.spi.AbstractDatacenter;
import net.java.dev.vcc.util.CompletedFuture;

import java.util.Collections;
import java.util.Set;

/**
 * A VMware ESX Datacenter.
 */
public class ViDatacenter extends AbstractDatacenter {

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
        return Collections.emptySet(); // TODO get commands
    }

    public Set<PowerState> getAllowedStates(PowerState from) {
        return Collections.emptySet(); // TODO get commands
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
