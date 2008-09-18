package net.java.dev.vcc;

import java.util.Set;
import java.util.concurrent.Future;

/**
 * Created by IntelliJ IDEA.
 * User: user
 * Date: 18-Sep-2008
 * Time: 08:26:56
 * To change this template use File | Settings | File Templates.
 */
public interface Computer {

    /**
     * Gets the host that this computer is currently attached to.
     * @return the host that this computer is currently attached to.
     */
    Host getHost();

    /**
     * Gets the power state of this computer.
     * @return the power state of this computer.
     */
    PowerState getState();

    /**
     * Gets the snapshots of this computer that are currently available.
     * @return the snapshots of this computer that are currently available.
     */
    Set<ComputerSnapshot> getSnapshots();

    /**
     * Gets the hosts that this computer can be migrated to.
     * @return the hosts that this computer can be migrated to.
     */
    Set<Host> getAllowedHosts();

    /**
     * Gets the name of this virtual computer.
     * @return the name of this virtual computer.
     */
    String getName();

    /**
     * Gets the description of this virtual computer or {@code null} if descriptions are not supported.
     * @return the description of this virtual computer or {@code null} if descriptions are not supported.
     */
    String getDescription();

    /**
     * Attempts to migrate the host to the specified host.
     * @param destination the host to migrate to.
     * @return a future for the operation being completed.
     */
    Future<Boolean> doSetHost(Host destination);

    Future<Boolean> doSetState(PowerState state);

    Future<PowerState> doPowerOn();

    Future<PowerState> doPowerOff(boolean hard);

    Future<PowerState> doSuspend();

    Future<PowerState> doResume();

    Future<ComputerSnapshot> doTakeSnapshot(String suggestedName, String suggestedDescription);

    Future<Boolean> doRevertToSnapshot(ComputerSnapshot snapshot);
}
