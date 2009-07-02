package net.java.dev.vcc.impl.vmware.esx;

import net.java.dev.vcc.api.Command;
import net.java.dev.vcc.api.Computer;
import net.java.dev.vcc.api.Host;
import net.java.dev.vcc.api.ManagedObjectId;
import net.java.dev.vcc.spi.AbstractHost;
import net.java.dev.vcc.util.CompletedFuture;

import java.util.Collections;
import java.util.Set;

public class ViHost extends AbstractHost {

    protected ViHost(ManagedObjectId<Host> id) {
        super(id);
    }

    public Set<Class<? extends Command>> getCommands() {
        return Collections.emptySet(); // TODO get commands
    }

    public <T extends Command> T execute(T command) {
        command.setSubmitted(new CompletedFuture("Unsupported command", new UnsupportedOperationException()));
        return command;
    }

    public Set<Computer> getComputers() {
        return Collections.emptySet();
    }

    public String getName() {
        return "";
    }
}