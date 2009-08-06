package net.java.dev.vcc.api;

import java.util.Set;

/**
 * Marker interface for a container of host resources.
 */
interface HostResourceContainer
{
    /**
     * Gets all the virtual computers that are contained in this container.
     *
     * @return the virtual computers that are contained in this container.
     */
    Set<Computer> getComputers();

    /**
     * Gets alll the {@link net.java.dev.vcc.api.HostResourceGroup}s that are contained in this container.
     *
     * @return the {@link net.java.dev.vcc.api.HostResourceGroup}s that are contained in this container.
     */
    Set<HostResourceGroup> getHostResourceGroups();

    /**
     * Gets all the virtual computers that are contained in this container including children.
     *
     * @return the virtual computers that are contained in this container including children.
     */
    Set<Computer> getAllComputers();

    /**
     * Gets all the {@link net.java.dev.vcc.api.HostResourceGroup}s that are contained in this container including children.
     *
     * @return the {@link net.java.dev.vcc.api.HostResourceGroup}s that are contained in this container including children.
     */
    Set<HostResourceGroup> getAllHostResourceGroups();
}