package net.java.dev.vcc;

import java.util.concurrent.Future;
import java.util.Set;

/**
 * Represents a snapshot of a virtual computer.
 */
public interface ComputerSnapshot {

    /**
     * Gets the name of the snapshot.
     * @return the name of the snapshot.
     */
    String getName();

    /**
     * Gets the description of the snapshot.
     * @return the description of the snapshot, or {@code null} if descriptions are not supported.
     */
    String getDescription();

    /**
     * Attempts to rename the snapshot.
     * @param name the new name.
     * @return a future for the operation which will return {@code Boolean.TRUE} if the operation was successful.
     */
    Future<Boolean> doRename(String name);

    /**
     * Attempts to modify the description of the snapshot.
     * @param description the new description.
     * @return a future for the operation which will return {@code Boolean.TRUE} if the operation was successful.
     */
    Future<Boolean> doSetDescription(String description);

    /**
     * Attempts to delete the snapshot.
     * @return a future for the operation which will return {@code Boolean.TRUE} if the operation was successful.
     */
    Future<Boolean> doDelete();

    /**
     * Gets the parent snapshot from which this snapshot was derived.
     * @return the parent snapshot or {@code null} if this snapshot does not have a parent.
     */
    ComputerSnapshot getParent();

    /**
     * Gets the child snapshots of this snapshot.
     * @return the collection of child snapshots (possibly empty).
     */
    Set<ComputerSnapshot> getChildren();
}
