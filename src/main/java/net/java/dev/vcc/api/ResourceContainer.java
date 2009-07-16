package net.java.dev.vcc.api;

import java.util.Set;

/**
 * Marker interface for a container of resources.
 */
public interface ResourceContainer
{
    /**
     * Gets all the hosts available for running virtual computers that are contained in this {@link net.java.dev.vcc.api.ResourceContainer}.
     *
     * @return the hosts available for running virtual computers that are contained in this {@link net.java.dev.vcc.api.ResourceContainer}.
     */
    Set<Host> getHosts();

    /**
     * Gets all the virtual computers that are contained in this {@link net.java.dev.vcc.api.ResourceContainer}.
     *
     * @return the virtual computers that are contained in this {@link net.java.dev.vcc.api.ResourceContainer}.
     */
    Set<Computer> getComputers();

    /**
     * Gets alll the {@link net.java.dev.vcc.api.ResourceGroup}s that are contained in this {@link net.java.dev.vcc.api.ResourceContainer}.
     *
     * @return the {@link net.java.dev.vcc.api.ResourceGroup}s that are contained in this {@link net.java.dev.vcc.api.ResourceContainer}.
     */
    Set<ResourceGroup> getResourceGroups();

    /**
     * Gets all the hosts available for running virtual computers that are contained in this {@link net.java.dev.vcc.api.ResourceContainer} including children.
     *
     * @return the hosts available for running virtual computers that are contained in this {@link net.java.dev.vcc.api.ResourceContainer} including children.
     */
    Set<Host> getAllHosts();

    /**
     * Gets all the virtual computers that are contained in this {@link net.java.dev.vcc.api.ResourceContainer} including children.
     *
     * @return the virtual computers that are contained in this {@link net.java.dev.vcc.api.ResourceContainer} including children.
     */
    Set<Computer> getAllComputers();

    /**
     * Gets all the {@link net.java.dev.vcc.api.ResourceGroup}s that are contained in this {@link net.java.dev.vcc.api.ResourceContainer} including children.
     *
     * @return the {@link net.java.dev.vcc.api.ResourceGroup}s that are contained in this {@link net.java.dev.vcc.api.ResourceContainer} including children.
     */
    Set<ResourceGroup> getAllResourceGroups();
}
