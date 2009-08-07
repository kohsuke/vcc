package net.java.dev.vcc.api;

import java.util.Set;
import java.util.concurrent.Future;

/**
 * Represents a snapshot of a virtual computer.
 */
public interface ComputerSnapshot extends ManagedObject<ComputerSnapshot> {

    /**
     * Attempts to rename the snapshot.
     *
     * @param name the new name.
     *
     * @return a future for the operation which will return {@code Boolean.TRUE} if the operation was successful.
     */
    Future<Boolean> doRename(String name);

    /**
     * Attempts to modify the description of the snapshot.
     *
     * @param description the new description.
     *
     * @return a future for the operation which will return {@code Boolean.TRUE} if the operation was successful.
     */
    Future<Boolean> doSetDescription(String description);

    /**
     * Attempts to delete the snapshot.
     *
     * @return a future for the operation which will return {@code Boolean.TRUE} if the operation was successful.
     */
    Future<Boolean> doDelete();

    /**
     * Gets the parent snapshot from which this snapshot was derived.
     *
     * @return the parent snapshot or {@code null} if this snapshot does not have a parent.
     */
    ComputerSnapshot getParent();

    /**
     * Gets the child snapshots of this snapshot.
     *
     * @return the collection of child snapshots (possibly empty).
     */
    Set<ComputerSnapshot> getChildren();

    /**
     * Gets the state of the virtual computer when the snapshot was taken.
     *
     * @return the state of the virtual computer when the snapshot was taken.
     */
    PowerState getState();
}
