package net.java.dev.vcc.ant;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import net.java.dev.vcc.api.Datacenter;
import net.java.dev.vcc.api.DatacenterManager;

import java.io.IOException;

/**
 * The base class for tasks that require a datacenter connection.
 */
public abstract class AbstractDatacenterTask extends Task {
    
    /**
     * The URI fo the datacenter.
     */
    private String datacenterUri;

    /**
     * The username to connect with.
     */
    private String username;

    /**
     * The password to connect with.
     */
    private String password;

    @Override
    public final void execute() throws BuildException {
        AntLogFactory.setTask(this);
        if ( datacenterUri == null )
        {
            throw new BuildException( "The datacenter URI must be specified the datacenteruri attribute" );
        }
        if ( username == null )
        {
            throw new BuildException( "The username must be specified the username attribute" );
        }
        if ( password == null )
        {
            throw new BuildException( "The password must be specified the password attribute" );
        }
        Datacenter datacenter = null;
        try
        {
            datacenter = DatacenterManager.getConnection( datacenterUri, username, password.toCharArray() );
            if ( datacenter == null )
            {
                throw new BuildException( "Unknown datacenter URI" );
            }
            execute( datacenter );
        }
        catch ( IOException e )
        {
            throw new BuildException( e.getMessage(), e );
        }
        finally
        {
            if ( datacenter != null )
            {
                datacenter.close();
            }
            AntLogFactory.setTask( null );
        }

    }

    /**
     * Execute the task on the specified datacenter.
     *
     * @param datacenter The datacenter connection.
     * @throws BuildException   when things go bad.
     */
    protected abstract void execute(Datacenter datacenter) throws BuildException;

    public final String getDatacenteruri() {
        return datacenterUri;
    }

    public final void setDatacenteruri(String datacenterUri) {
        this.datacenterUri = datacenterUri;
    }

    public final String getUsername() {
        return username;
    }

    public final void setUsername(String username) {
        this.username = username;
    }

    public final String getPassword() {
        return password;
    }

    public final void setPassword(String password) {
        this.password = password;
    }
}
