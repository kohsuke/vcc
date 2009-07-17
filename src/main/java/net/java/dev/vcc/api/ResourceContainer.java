package net.java.dev.vcc.api;

import java.util.Set;

/**
 * Marker interface for a container of resources.
 */
interface ResourceContainer
{
    /**
     * Gets all the hosts available for running virtual computers that are contained in this container.
     *
     * @return the hosts available for running virtual computers that are contained in this container.
     */
    Set<Host> getHosts();

    /**
     * Gets all the virtual computers that are contained in this container.
     *
     * @return the virtual computers that are contained in this container.
     */
    Set<Computer> getComputers();

    /**
     * Gets alll the {@link net.java.dev.vcc.api.ResourceGroup}s that are contained in this container.
     *
     * @return the {@link net.java.dev.vcc.api.ResourceGroup}s that are contained in this container.
     */
    Set<ResourceGroup> getResourceGroups();

    /**
     * Gets all the hosts available for running virtual computers that are contained in this container including children.
     *
     * @return the hosts available for running virtual computers that are contained in this container including children.
     */
    Set<Host> getAllHosts();

    /**
     * Gets all the virtual computers that are contained in this container including children.
     *
     * @return the virtual computers that are contained in this container including children.
     */
    Set<Computer> getAllComputers();

    /**
     * Gets all the {@link net.java.dev.vcc.api.ResourceGroup}s that are contained in this container including children.
     *
     * @return the {@link net.java.dev.vcc.api.ResourceGroup}s that are contained in this container including children.
     */
    Set<ResourceGroup> getAllResourceGroups();
}
