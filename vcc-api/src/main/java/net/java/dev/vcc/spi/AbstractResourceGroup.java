package net.java.dev.vcc.spi;

import net.java.dev.vcc.api.ResourceGroup;
import net.java.dev.vcc.api.Host;
import net.java.dev.vcc.api.Computer;
import net.java.dev.vcc.api.ManagedObjectId;

import java.util.Set;
import java.util.HashSet;

/**
 * The base class from which all Service Provider Implementations map a resource group from.
 */
public abstract class AbstractResourceGroup extends AbstractManagedObject<ResourceGroup> implements ResourceGroup
{
    protected AbstractResourceGroup( ManagedObjectId<ResourceGroup> resourceGroupManagedObjectId )
    {
        super( resourceGroupManagedObjectId );
    }

    /**
     * {@inheritDoc}
     */
    public Set<Host> getAllHosts()
    {
        Set<Host> result = new HashSet<Host>( getHosts() );
        for (ResourceGroup group: getResourceGroups()) {
            result.addAll( group.getAllHosts() );
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Set<Computer> getAllComputers()
    {
        Set<Computer> result = new HashSet<Computer>( getComputers() );
        for (ResourceGroup group: getResourceGroups()) {
            result.addAll( group.getAllComputers() );
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Set<ResourceGroup> getAllResourceGroups()
    {
        Set<ResourceGroup> result = new HashSet<ResourceGroup>( getResourceGroups() );
        for (ResourceGroup group: getResourceGroups()) {
            result.addAll( group.getAllResourceGroups() );
        }
        return result;
    }

    
}
