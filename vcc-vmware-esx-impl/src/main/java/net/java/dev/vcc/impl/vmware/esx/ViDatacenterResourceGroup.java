package net.java.dev.vcc.impl.vmware.esx;

import net.java.dev.vcc.api.Command;
import net.java.dev.vcc.api.ComputerTemplate;
import net.java.dev.vcc.api.DatacenterResourceGroup;
import net.java.dev.vcc.api.Host;
import net.java.dev.vcc.api.ManagedObjectId;
import net.java.dev.vcc.spi.AbstractDatacenterResourceGroup;
import net.java.dev.vcc.util.CompletedFuture;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class ViDatacenterResourceGroup
        extends AbstractDatacenterResourceGroup {

    private final ViDatacenter datacenter;

    private ViDatacenterResourceGroup parent;

    private String name;

    private final Map<ViHostId, ViHost> hosts = Collections.synchronizedMap(new HashMap<ViHostId, ViHost>());

    private final Map<ViComputerTemplateId, ViComputerTemplate> templates = Collections
            .synchronizedMap(new HashMap<ViComputerTemplateId, ViComputerTemplate>());

    private final Map<ViDatacenterResourceGroupId, ViDatacenterResourceGroup> resourceGroups =
            Collections.synchronizedMap(new HashMap<ViDatacenterResourceGroupId, ViDatacenterResourceGroup>());

    ViDatacenterResourceGroup(ViDatacenter datacenter, ManagedObjectId<DatacenterResourceGroup> id,
                              ViDatacenterResourceGroup parent, String name) {
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

    public Set<DatacenterResourceGroup> getDatacenterResourceGroups() {
        return Collections.unmodifiableSet(new HashSet<DatacenterResourceGroup>(resourceGroups.values()));
    }

    public Set<Host> getHosts() {
        return Collections.unmodifiableSet(new HashSet<Host>(hosts.values()));
    }

    @Override
    public Set<ComputerTemplate> getComputerTemplates() {
        return Collections.unmodifiableSet(new HashSet<ComputerTemplate>(templates.values()));
    }

    public String getName() {
        return this.name;
    }

    void setName(String name) {
        this.name = name;
    }

    void setParent(ViDatacenterResourceGroup parent) {
        this.parent = parent;
    }

    void addHost(ViHost viHost) {
        hosts.put(viHost.getId(), viHost);
    }

    void removeHost(ViHost viHost) {
        hosts.remove(viHost.getId());
    }

    public void addResourceGroup(ViDatacenterResourceGroup viResourceGroup) {
        resourceGroups.put(viResourceGroup.getId(), viResourceGroup);
    }

    public void removeResourceGroup(ViDatacenterResourceGroup viResourceGroup) {
        resourceGroups.remove(viResourceGroup.getId());
    }

    public void addComputerTemplate(ViComputerTemplate computerTemplate) {
        templates.put(computerTemplate.getId(), computerTemplate);
    }

    public void removeComputerTemplate(ViComputerTemplate computerTemplate) {
        templates.remove(computerTemplate.getId());
    }

    @Override
    public ViDatacenterResourceGroupId getId() {
        return (ViDatacenterResourceGroupId) super.getId();
    }
}