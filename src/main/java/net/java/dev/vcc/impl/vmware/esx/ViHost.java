package net.java.dev.vcc.impl.vmware.esx;

import net.java.dev.vcc.api.Command;
import net.java.dev.vcc.api.Computer;
import net.java.dev.vcc.api.HostResourceGroup;
import net.java.dev.vcc.spi.AbstractHost;
import net.java.dev.vcc.util.CompletedFuture;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class ViHost extends AbstractHost {

    private final ViDatacenter datacenter;

    private String name;

    private ViDatacenterResourceGroup parent;

    private final Map<ViComputerId, ViComputer> computers =
            Collections.synchronizedMap(new HashMap<ViComputerId, ViComputer>());

    private final Map<ViHostResourceGroupId, ViHostResourceGroup> resourceGroups =
            Collections.synchronizedMap(new HashMap<ViHostResourceGroupId, ViHostResourceGroup>());

    ViHost(ViDatacenter datacenter, ViHostId id, ViDatacenterResourceGroup parent, String name) {
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

    public Set<HostResourceGroup> getHostResourceGroups() {
        return Collections.unmodifiableSet(new HashSet<HostResourceGroup>(resourceGroups.values()));
    }

    public Set<Computer> getComputers() {
        return Collections.unmodifiableSet(new HashSet<Computer>(computers.values()));
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    void setParent(ViDatacenterResourceGroup parent) {
        this.parent = parent;
    }

    void addComputer(ViComputer viComputer) {
        computers.put(viComputer.getId(), viComputer);
    }

    public void addHostResourceGroup(ViHostResourceGroup viResourceGroup) {
        resourceGroups.put(viResourceGroup.getId(), viResourceGroup);
    }

    void removeComputer(ViComputer viComputer) {
        computers.remove(viComputer.getId());
    }

    public void removeResourceGroup(ViHostResourceGroup viResourceGroup) {
        resourceGroups.remove(viResourceGroup.getId());
    }

    @Override
    public ViHostId getId() {
        return (ViHostId) super.getId();
    }
}
