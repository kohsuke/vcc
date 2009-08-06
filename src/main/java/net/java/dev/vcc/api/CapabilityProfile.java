package net.java.dev.vcc.api;

import java.util.Set;

/**
 * Describes the capabilities of a {@link net.java.dev.vcc.api.Datacenter}.
 */
public interface CapabilityProfile {
    /**
     * Returns the types of {@link Command} that are supported against a specific type of
     * {@link net.java.dev.vcc.api.ManagedObject}.
     *
     * @param managedObjectClass The type of {@link net.java.dev.vcc.api.ManagedObject}.
     * @return The types of {@link net.java.dev.vcc.api.Command} that are supported on the
     *         {@link net.java.dev.vcc.api.ManagedObject}
     */
    Set<Class<? extends Command>> getCommands(Class<? extends ManagedObject> managedObjectClass);

    /**
     * Returns the types of {@link net.java.dev.vcc.api.ManagedObject} that are available.
     *
     * @return the types of {@link net.java.dev.vcc.api.ManagedObject} that are available.
     */
    Set<Class<? extends ManagedObject>> getObjectClasses();

    /**
     * Returns {@code true} if this {@link net.java.dev.vcc.api.CapabilityProfile} supports a superset of the
     * supplied {@link net.java.dev.vcc.api.CapabilityProfile}.
     *
     * @param that The {@link net.java.dev.vcc.api.CapabilityProfile} which might be a subset of this.
     * @return {@code true} if this {@link net.java.dev.vcc.api.CapabilityProfile} supports a superset of the
     *         supplied {@link net.java.dev.vcc.api.CapabilityProfile}.
     */
    boolean supports(CapabilityProfile that);
}
