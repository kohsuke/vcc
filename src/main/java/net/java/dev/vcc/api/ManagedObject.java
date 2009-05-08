package net.java.dev.vcc.api;

import java.util.Set;

/**
 * Represents an object in a {@link net.java.dev.vcc.api.Datacenter}
 */
public interface ManagedObject {
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
     * @return the command parameter (to support method chaining).
     */
    <T extends Command> T execute(T command);
}
