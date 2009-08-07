package net.java.dev.vcc.impl.vmware.esx;

import net.java.dev.vcc.api.Command;
import net.java.dev.vcc.spi.AbstractComputerTemplate;
import net.java.dev.vcc.util.CompletedFuture;

import java.util.Collections;
import java.util.Set;

final class ViComputerTemplate
        extends AbstractComputerTemplate {

    private final ViDatacenter datacenter;

    private ViDatacenterResourceGroup parent;

    private String name;

    ViComputerTemplate(ViDatacenter datacenter, ViComputerTemplateId id, ViDatacenterResourceGroup parent,
                       String name) {
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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return null;
    }

    @Override
    public ViComputerTemplateId getId() {
        return (ViComputerTemplateId) super.getId();
    }

    void setParent(ViDatacenterResourceGroup parent) {
        this.parent = parent;
    }

    void setName(String name) {
        this.name = name;
    }
}