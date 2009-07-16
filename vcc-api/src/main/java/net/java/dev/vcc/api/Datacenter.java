package net.java.dev.vcc.api;

import java.util.Set;

/**
 * Represeents a datacenter of {@link Host}s for virtual {@link Computer}s.
 *
 * @author Stephen Connolly
 */
public interface Datacenter extends ManagedObject<Datacenter>, CapabilityProfile, ResourceContainer {

    /**
     * Gets all the power states that are supported by this connection.
     *
     * @return the power states that are supported by this connection.
     */
    Set<PowerState> getAllowedStates();

    /**
     * Gets all the power states that can be transitioned to from a specific power state.
     *
     * @param from the power state to transition from.
     * @return the power states that a computer can be transitioned to.
     */
    Set<PowerState> getAllowedStates(PowerState from);

    /**
     * Closes the connection releasing any handles.
     */
    void close();

    /**
     * Gets the state of the connection.
     *
     * @return {@code true} if the connection is open.
     */
    boolean isOpen();

}
