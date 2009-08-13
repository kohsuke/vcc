package net.java.dev.vcc.ant;

import net.java.dev.vcc.api.Datacenter;
import net.java.dev.vcc.api.DatacenterManager;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

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
        if (datacenterUri == null) {
            throw new BuildException("The datacenter URI must be specified the datacenteruri attribute");
        }
        if (username == null) {
            throw new BuildException("The username must be specified the username attribute");
        }
        if (password == null) {
            throw new BuildException("The password must be specified the password attribute");
        }

        ClassLoader loader = getClass().getClassLoader();

        if (loader instanceof URLClassLoader) {
            log("Using " + loader + " as classloader", Project.MSG_DEBUG);
            for (URL url : ((URLClassLoader) loader).getURLs()) {
                log("  " + url, Project.MSG_INFO);
            }
        } else {
            log("Using " + loader + " as classloader", Project.MSG_DEBUG);
        }

        try {
            ClassLoaderFixerThread fixer = new ClassLoaderFixerThread();
            fixer.setContextClassLoader(loader);
            fixer.start();
            try {
                fixer.join();
            } catch (InterruptedException e) {
                throw new BuildException(e.getMessage(), e);
            }
            if (fixer.datacenter == null) {
                throw new BuildException("Unknown datacenter URI");
            }
            if (fixer.ioException != null) {
                throw new BuildException(fixer.ioException.getMessage(), fixer.ioException);
            }
        } finally {
            AntLogFactory.setTask(null);
        }

    }

    private final class ClassLoaderFixerThread extends Thread {
        private volatile Datacenter datacenter = null;
        private volatile IOException ioException = null;

        @Override
        public void run() {
            try {
                datacenter = DatacenterManager
                        .getConnection(datacenterUri, username, password.toCharArray());
                if (datacenter == null) {
                    return;
                }
                execute(datacenter);
            } catch (IOException e) {
                ioException = e;
            } finally {
                if (datacenter != null) {
                    datacenter.close();
                }
                AntLogFactory.setTask(null);
            }
        }
    }

    /**
     * Execute the task on the specified datacenter.
     *
     * @param datacenter The datacenter connection.
     *
     * @throws BuildException when things go bad.
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
