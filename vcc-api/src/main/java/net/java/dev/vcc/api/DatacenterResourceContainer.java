package net.java.dev.vcc.api;

import java.util.Set;

/**
 * Marker interface for a container of datacenter resources.
 */
interface DatacenterResourceContainer
{
    /**
     * Gets all the hosts available for running virtual computers that are contained in this container.
     *
     * @return the hosts available for running virtual computers that are contained in this container.
     */
    Set<Host> getHosts();

    /**
     * Gets all the hosts available for running virtual computers that are contained in this container including children.
     *
     * @return the hosts available for running virtual computers that are contained in this container including children.
     */
    Set<Host> getAllHosts();

    /**
     * Gets alll the {@link DatacenterResourceGroup}s that are contained in this container.
     *
     * @return the {@link DatacenterResourceGroup}s that are contained in this container.
     */
    Set<DatacenterResourceGroup> getDatacenterResourceGroups();

    /**
     * Gets all the {@link DatacenterResourceGroup}s that are contained in this container including children.
     *
     * @return the {@link DatacenterResourceGroup}s that are contained in this container including children.
     */
    Set<DatacenterResourceGroup> getAllDatacenterResourceGroups();

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
