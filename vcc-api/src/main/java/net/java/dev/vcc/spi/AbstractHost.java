package net.java.dev.vcc.spi;

import net.java.dev.vcc.api.Computer;
import net.java.dev.vcc.api.Host;
import net.java.dev.vcc.api.ManagedObjectId;
import net.java.dev.vcc.api.ResourceGroup;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA. User: user Date: 30-Apr-2009 Time: 17:40:33 To change this template use File | Settings |
 * File Templates.
 */
public abstract class AbstractHost extends AbstractManagedObject<Host> implements Host {
    protected AbstractHost(ManagedObjectId<Host> id) {
        super(id);
    }

    /**
     * {@inheritDoc}
     */
    public Set<Host> getAllHosts() {
        Set<Host> result = new HashSet<Host>(getHosts());
        for (ResourceGroup group : getResourceGroups()) {
            result.addAll(group.getAllHosts());
        }
        return result;
    }

    public Set<Host> getHosts() {
        return Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     */
    public Set<Computer> getAllComputers() {
        Set<Computer> result = new HashSet<Computer>(getComputers());
        for (ResourceGroup group : getResourceGroups()) {
            result.addAll(group.getAllComputers());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Set<ResourceGroup> getAllResourceGroups() {
        Set<ResourceGroup> result = new HashSet<ResourceGroup>(getResourceGroups());
        for (ResourceGroup group : getResourceGroups()) {
            result.addAll(group.getAllResourceGroups());
        }
        return result;
    }

    public Set<ResourceGroup> getResourceGroups() {
        return Collections.emptySet();
    }
}
