package net.java.dev.vcc.ant;

import net.java.dev.vcc.api.Datacenter;
import net.java.dev.vcc.api.Success;
import net.java.dev.vcc.api.Computer;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by IntelliJ IDEA. User: connollys Date: Aug 11, 2009 Time: 3:42:14 PM To change this template use File |
 * Settings | File Templates.
 */
public abstract class AbstractComputerActionTask extends AbstractDatacenterTask {
    /**
     * The computers to process.
     */
    private List<ComputerElement> computers = new ArrayList<ComputerElement>();

    /**
     * The number of seconds to wait for operations to complete.
     */
    private int timeout = 60;

    /**
     * {@inheritDoc}
     */
    protected final void execute(Datacenter datacenter)
            throws BuildException {
        Set<String> targets = new HashSet<String>();
        if (computers != null) {
            for (ComputerElement c: computers) {
                targets.add(c.getName());
            }
        }
        Map<String, Future<Success>> results = new LinkedHashMap<String, Future<Success>>();
        for (Computer c : datacenter.getAllComputers()) {
            if (targets.contains(c.getId().toString())) {
                log("Computer " + c.getName() + " is in state " + c.getState(), Project.MSG_DEBUG);
                results.put(c.getName(), doAction(c));
                targets.remove(c.getName());
            }
        }
        if (!targets.isEmpty()) {
            throw new BuildException("Could not find the following computers: " + targets);
        }
        long giveUp = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(timeout);
        while (System.currentTimeMillis() < giveUp && !results.isEmpty()) {
            for (Iterator<Map.Entry<String, Future<Success>>> it = results.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, Future<Success>> entry = it.next();
                try {
                    entry.getValue().get(giveUp - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                    recordSuccess(entry.getKey());
                    it.remove();
                }
                catch (ExecutionException e) {
                    recordFailure(entry.getKey());
                    it.remove();
                    throw new BuildException(e.getMessage(), e);
                }
                catch (TimeoutException e) {
                    throw new BuildException(e.getMessage(), e);
                }
                catch (InterruptedException e) {
                    throw new BuildException(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Records that the operation on the named computer failed.
     *
     * @param name The computer name.
     *
     * @since 1.0-alpha-1
     */
    protected abstract void recordFailure(String name);

    /**
     * Records that the operation on the named computer succeeded.
     *
     * @param name The computer name.
     *
     * @since 1.0-alpha-1
     */
    protected abstract void recordSuccess(String name);

    /**
     * Preforms the operation on the specified computer, returning a future for completion of the operation.
     *
     * @param computer The computer.
     *
     * @return A future for completion of the operation.
     *
     * @since 1.0-alpha-1
     */
    protected abstract Future<Success> doAction(Computer computer);

    public final int getTimeout() {
        return timeout;
    }

    public final void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public final void addConfiguredComputer(ComputerElement aComputer) {
        computers.add(aComputer);
    }
}
