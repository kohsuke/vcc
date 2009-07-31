package net.java.dev.vcc.impl.vmware.esx;

import net.java.dev.vcc.api.Command;
import net.java.dev.vcc.api.Computer;
import net.java.dev.vcc.api.Host;
import net.java.dev.vcc.api.ManagedObjectId;
import net.java.dev.vcc.api.ResourceGroup;
import net.java.dev.vcc.spi.AbstractResourceGroup;
import net.java.dev.vcc.util.CompletedFuture;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class ViResourceGroup extends AbstractResourceGroup {

    private final ViDatacenter datacenter;
    private ViResourceGroup parent;
    private String name;
    private final Map<ViHostId, ViHost> hosts = Collections.synchronizedMap(new HashMap<ViHostId, ViHost>());
    private final Map<ViComputerId, ViComputer> computers = Collections
            .synchronizedMap(new HashMap<ViComputerId, ViComputer>());
    private final Map<ViResourceGroupId, ViResourceGroup> resourceGroups = Collections
            .synchronizedMap(new HashMap<ViResourceGroupId, ViResourceGroup>());

    ViResourceGroup(ViDatacenter datacenter, ManagedObjectId<ResourceGroup> id,
                    ViResourceGroup parent, String name) {
        super(id);
        this.datacenter = datacenter;
        this.parent = parent;
        this.name = name;
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
        return Collections.unmodifiableSet(new HashSet<Computer>(computers.values()));
    }

    public String getName() {
        return this.name;
    }

    void setName(String name) {
        this.name = name;
    }

    void setParent(ViResourceGroup parent) {
        this.parent = parent;
    }

    void addComputer(ViComputer viComputer) {
        computers.put(viComputer.getId(), viComputer);
    }

    public void addResourceGroup(ViResourceGroup viResourceGroup) {
        resourceGroups.put(viResourceGroup.getId(), viResourceGroup);
    }

    void removeComputer(ViComputer viComputer) {
        computers.remove(viComputer.getId());
    }

    public void removeResourceGroup(ViResourceGroup viResourceGroup) {
        resourceGroups.remove(viResourceGroup.getId());
    }

    @Override
    public ViResourceGroupId getId() {
        return (ViResourceGroupId) super.getId();
    }
}