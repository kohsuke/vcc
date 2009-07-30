package net.java.dev.vcc.impl.vmware.esx;

import net.java.dev.vcc.api.Command;
import net.java.dev.vcc.api.Computer;
import net.java.dev.vcc.api.Host;
import net.java.dev.vcc.api.ManagedObjectId;
import net.java.dev.vcc.api.ResourceGroup;
import net.java.dev.vcc.spi.AbstractResourceGroup;
import net.java.dev.vcc.util.CompletedFuture;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ViResourceGroup extends AbstractResourceGroup {

    private final ViDatacenter datacenter;
    private ViResourceGroup parent;
    private final Set<ViHost> hosts = Collections.synchronizedSet(new HashSet<ViHost>());
    private final Set<ViComputer> computers = Collections.synchronizedSet(new HashSet<ViComputer>());
    private final Set<ViResourceGroup> resourceGroups = Collections.synchronizedSet(new HashSet<ViResourceGroup>());

    ViResourceGroup(ViDatacenter datacenter, ManagedObjectId<ResourceGroup> id,
                    ViResourceGroup parent) {
        super(id);
        this.datacenter = datacenter;
        this.parent = parent;
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

    public Set<ResourceGroup> getResourceGroups() {
        return Collections.unmodifiableSet(new HashSet<ResourceGroup>(resourceGroups));
    }

    public Set<Computer> getComputers() {
        return Collections.unmodifiableSet(new HashSet<Computer>(computers));
    }

    public String getName() {
        return "";
    }
}