package net.java.dev.vcc;

import java.util.Set;
import java.util.concurrent.Future;

/**
 * Represents a Virtual Computer.
 */
public interface Computer {

    /**
     * Gets the host that this computer is currently attached to.
     *
     * @return the host that this computer is currently attached to.
     */
    Host getHost();

    /**
     * Gets the power state of this computer.
     *
     * @return the power state of this computer.
     */
    PowerState getState();

    /**
     * Gets the previous power state of this computer.
     *
     * @return the previous power state of this computer.
     */
    PowerState getPreviousState();

    /**
     * Gets the next power state of this computer. This will always be {@link PowerState#UNKNOWN} unless
     * {@link #getState()} == {@link PowerState#TRANSITIONAL}
     *
     * @return the next power state of this computer.
     */
    PowerState getNextState();

    /**
     * Gets the snapshots of this computer that are currently available.
     *
     * @return the snapshots of this computer that are currently available.
     */
    Set<ComputerSnapshot> getSnapshots();

    /**
     * Gets the hosts that this computer can be migrated to.
     *
     * @return the hosts that this computer can be migrated to.
     */
    Set<Host> getAllowedHosts();

    /**
     * Gets the name of this virtual computer.
     *
     * @return the name of this virtual computer.
     */
    String getName();

    /**
     * Gets the description of this virtual computer or {@code null} if descriptions are not supported.
     *
     * @return the description of this virtual computer or {@code null} if descriptions are not supported.
     */
    String getDescription();

    /**
     * Attempts to migrate the host to the specified host.
     *
     * @param destination the host to migrate to.
     * @return a future for the operation being completed.
     */
    Future<Boolean> doMigrate(Host destination);

    /**
     * Powers on the virtual computer.
     *
     * @return a future for the operation being completed.
     */
    Future<PowerState> doPowerOn();

    /**
     * Requests that the virtual computer power off. This is equivalent to {@link #doPowerOff(boolean)} with the
     * parameter {@code true}.
     *
     * @return a future for the operation being completed.
     */
    Future<PowerState> doPowerOff();

    /**
     * Turn the virtual machine off.
     *
     * @param hard if {@code true} then the virtual machine will be turned off immediately (i.e. yank the power cord).
     *             if {@code false} then the virtual machine will be told to turn off (i.e. pressing the power off ACPI button which
     *             a good operating system will notice and initiate an orderly shutdown)
     * @return a future for the operation being completed.
     */
    Future<PowerState> doPowerOff(boolean hard);

    /**
     * Suspends the virtual machine.
     *
     * @return a future for the operation being completed.
     */
    Future<PowerState> doSuspend();

    /**
     * Resumes a virtual computer from the suspended or paused state.
     *
     * @return a future for the operation being completed.
     */
    Future<PowerState> doResume();

    /**
     * Pauses a virtual computer.
     *
     * @return a future for the operation being completed.
     */
    Future<PowerState> doPause();

    /**
     * Takes a snapshot of the virtual computer.  This may overwrite an existing snapshot if the provider
     * only supports a single snapshot per virtual computer.
     *
     * @param suggestedName        The name to try and assign to the snapshot.
     * @param suggestedDescription The description to try and assign to the snapshot.
     * @return a future for the operation being completed.
     */
    Future<ComputerSnapshot> doTakeSnapshot(String suggestedName, String suggestedDescription);

    /**
     * Reverts the virtual computer to the specified snapshot.
     *
     * @param snapshot the snapshot to revert to.
     * @return a future for the operation being completed.
     */
    Future<Boolean> doRevertToSnapshot(ComputerSnapshot snapshot);
}
