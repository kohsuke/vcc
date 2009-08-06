package net.java.dev.vcc.spi;

import net.java.dev.vcc.api.Computer;
import net.java.dev.vcc.api.DatacenterResourceGroup;
import net.java.dev.vcc.api.Host;
import net.java.dev.vcc.api.HostResourceGroup;
import net.java.dev.vcc.api.ManagedObjectId;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The base class from which all Service Provider Implementations map a resource group from.
 */
public abstract class AbstractDatacenterResourceGroup
    extends AbstractManagedObject<DatacenterResourceGroup>
    implements DatacenterResourceGroup
{
    protected AbstractDatacenterResourceGroup( ManagedObjectId<DatacenterResourceGroup> resourceGroupManagedObjectId )
    {
        super( resourceGroupManagedObjectId );
    }

    /**
     * {@inheritDoc}
     */
    public Set<Host> getHosts()
    {
        return Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     */
    public Set<DatacenterResourceGroup> getDatacenterResourceGroups()
    {
        return Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     */
    public Set<Computer> getAllComputers()
    {
        Set<Computer> result = new HashSet<Computer>();
        for ( DatacenterResourceGroup group : getDatacenterResourceGroups() )
        {
            result.addAll( group.getAllComputers() );
        }
        for ( Host host : getHosts() )
        {
            result.addAll( host.getAllComputers() );
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Set<Host> getAllHosts()
    {
        Set<Host> result = new HashSet<Host>( getHosts() );
        for ( DatacenterResourceGroup group : getDatacenterResourceGroups() )
        {
            result.addAll( group.getAllHosts() );
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Set<HostResourceGroup> getAllHostResourceGroups()
    {
        Set<HostResourceGroup> result = new HashSet<HostResourceGroup>();
        for ( DatacenterResourceGroup group : getDatacenterResourceGroups() )
        {
            result.addAll( group.getAllHostResourceGroups() );
        }
        for ( Host host : getHosts() )
        {
            result.addAll( host.getAllHostResourceGroups() );
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Set<DatacenterResourceGroup> getAllDatacenterResourceGroups()
    {
        Set<DatacenterResourceGroup> result = new HashSet<DatacenterResourceGroup>( getDatacenterResourceGroups() );
        for ( DatacenterResourceGroup group : getDatacenterResourceGroups() )
        {
            result.addAll( group.getAllDatacenterResourceGroups() );
        }
        return result;
    }


}
