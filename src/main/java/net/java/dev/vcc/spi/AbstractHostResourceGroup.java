package net.java.dev.vcc.spi;

import net.java.dev.vcc.api.Computer;
import net.java.dev.vcc.api.HostResourceGroup;
import net.java.dev.vcc.api.ManagedObjectId;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The base class from which all Service Provider Implementations map a resource group from.
 */
public abstract class AbstractHostResourceGroup
    extends AbstractManagedObject<HostResourceGroup>
    implements HostResourceGroup
{
    protected AbstractHostResourceGroup( ManagedObjectId<HostResourceGroup> resourceGroupManagedObjectId )
    {
        super( resourceGroupManagedObjectId );
    }

    /**
     * {@inheritDoc}
     */
    public Set<Computer> getComputers()
    {
        return Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     */
    public Set<HostResourceGroup> getHostResourceGroups()
    {
        return Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     */
    public Set<HostResourceGroup> getAllHostResourceGroups()
    {
        Set<HostResourceGroup> result = new HashSet<HostResourceGroup>( getHostResourceGroups() );
        for ( HostResourceGroup group : getHostResourceGroups() )
        {
            result.addAll( group.getAllHostResourceGroups() );
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Set<Computer> getAllComputers()
    {
        Set<Computer> result = new HashSet<Computer>( getComputers() );
        for ( HostResourceGroup group : getHostResourceGroups() )
        {
            result.addAll( group.getAllComputers() );
        }
        return result;
    }

}