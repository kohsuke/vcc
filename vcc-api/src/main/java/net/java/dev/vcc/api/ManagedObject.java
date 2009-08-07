package net.java.dev.vcc.api;

import java.util.Set;

/**
 * Represents an object in a {@link net.java.dev.vcc.api.Datacenter}
 */
public interface ManagedObject<T extends ManagedObject<T>> {

    ManagedObjectId<T> getId();

    /**
     * Returns the types of {@link Command} that are supported on this Managed Object.
     *
     * @return the types of {@link Command} that are supported on this Managed Object.
     */
    Set<Class<? extends Command>> getCommands();

    /**
     * Executes a command against this managed object.
     *
     * @param command the command to execute.
     * @param <T>     the type of this command.
     *
     * @return the command parameter (to support method chaining).
     */
    <T extends Command> T execute(T command);

    /**
     * Gets the name of this managed object.
     *
     * @return the name of this managed object.
     */
    String getName();

    /**
     * Gets the description of this managed object or {@code null} if descriptions are not supported.
     *
     * @return the description of this managed object or {@code null} if descriptions are not supported.
     */
    String getDescription();
}
