package net.java.dev.vcc.test;

import net.java.dev.vcc.api.Command;
import net.java.dev.vcc.api.Computer;
import net.java.dev.vcc.api.Datacenter;
import net.java.dev.vcc.api.DatacenterResourceGroup;
import net.java.dev.vcc.api.Host;
import net.java.dev.vcc.api.ManagedObjectId;
import net.java.dev.vcc.api.PowerState;
import net.java.dev.vcc.spi.AbstractDatacenter;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A useless datacenter.
 */
public class CrappyDatacenter
    extends AbstractDatacenter
{


    private final Object lock = new Object();

    private boolean open = true;

    public CrappyDatacenter( CrappyDatacenterId crappyDatacenterId )
    {
        super( crappyDatacenterId );
    }

    public Set<Host> getHosts()
    {
        checkOpen();
        return Collections.emptySet();
    }

    public Set<Computer> getComputers()
    {
        checkOpen();
        return Collections.emptySet();
    }

    public Set<DatacenterResourceGroup> getDatacenterResourceGroups()
    {
        checkOpen();
        return Collections.emptySet();
    }

    public Set<PowerState> getAllowedStates()
    {
        checkOpen();
        return Collections.singleton( PowerState.STOPPED );
    }

    public Set<PowerState> getAllowedStates( PowerState from )
    {
        checkOpen();
        return Collections.singleton( PowerState.STOPPED );
    }

    public void close()
    {
        synchronized ( lock )
        {
            open = false;
        }
    }

    public boolean isOpen()
    {
        synchronized ( lock )
        {
            return open;
        }
    }

    private void checkOpen()
    {
        synchronized ( lock )
        {
            if ( !open )
            {
                throw new IllegalStateException( "Connection is already closed" );
            }
        }
    }

    public ManagedObjectId<Datacenter> getId()
    {
        return null;
    }

    public Set<Class<? extends Command>> getCommands()
    {
        checkOpen();
        return Collections.emptySet();
    }

    @SuppressWarnings("unchecked")
    public <T extends Command> T execute( T command )
    {
        checkOpen();
        command.setSubmitted( new Future()
        {
            public boolean cancel( boolean mayInterruptIfRunning )
            {
                return false;
            }

            public boolean isCancelled()
            {
                return false;
            }

            public boolean isDone()
            {
                return true;
            }

            public Object get()
                throws InterruptedException, ExecutionException
            {
                return null;
            }

            public Object get( long timeout, TimeUnit unit )
                throws InterruptedException, ExecutionException, TimeoutException
            {
                return null;
            }
        } );
        return command;
    }
}
