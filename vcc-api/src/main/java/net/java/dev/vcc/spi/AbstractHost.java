package net.java.dev.vcc.spi;

import net.java.dev.vcc.api.Computer;
import net.java.dev.vcc.api.ComputerTemplate;
import net.java.dev.vcc.api.Host;
import net.java.dev.vcc.api.HostResourceGroup;
import net.java.dev.vcc.api.ManagedObjectId;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA. User: user Date: 30-Apr-2009 Time: 17:40:33 To change this template use File | Settings |
 * File Templates.
 */
public abstract class AbstractHost
        extends AbstractManagedObject<Host>
        implements Host {
    protected AbstractHost(ManagedObjectId<Host> id) {
        super(id);
    }

    /**
     * {@inheritDoc}
     */
    public Set<ComputerTemplate> getComputerTemplates() {
        return Collections.emptySet();
    }

    public Set<Computer> getComputers() {
        return Collections.emptySet();
    }

    public Set<HostResourceGroup> getHostResourceGroups() {
        return Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     */
    public Set<ComputerTemplate> getAllComputerTemplates() {
        Set<ComputerTemplate> result = new HashSet<ComputerTemplate>(getComputerTemplates());
        for (HostResourceGroup group : getHostResourceGroups()) {
            result.addAll(group.getAllComputerTemplates());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Set<HostResourceGroup> getAllHostResourceGroups() {
        Set<HostResourceGroup> result = new HashSet<HostResourceGroup>(getHostResourceGroups());
        for (HostResourceGroup group : getHostResourceGroups()) {
            result.addAll(group.getAllHostResourceGroups());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Set<Computer> getAllComputers() {
        Set<Computer> result = new HashSet<Computer>(getComputers());
        for (HostResourceGroup group : getHostResourceGroups()) {
            result.addAll(group.getAllComputers());
        }
        return result;
    }

}
