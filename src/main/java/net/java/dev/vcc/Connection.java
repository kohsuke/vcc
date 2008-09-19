package net.java.dev.vcc;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: user
 * Date: 17-Sep-2008
 * Time: 17:45:26
 * To change this template use File | Settings | File Templates.
 */
public interface Connection {

    /**
     * Gets all the hosts available for running virtual computers that are controlled by this connection.
     * @return the hosts available for running virtual computers that are controlled by this connection.
     */
    Set<Host> getHosts();

    /**
     * Gets all the virtual computers that are controllable from this connection.
     * @return the virtual computers that are controllable from this connection.
     */
    Set<Computer> getComputers();

    /**
     * Gets all the power states that are supported by this connection.
     * @return the power states that are supported by this connection.
     */
    Set<PowerState> getAllowedStates();

    /**
     * Gets all the power states that can be transitioned to from a specific power state.
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
     * @return {@code true} if the connection is open.
     */
    boolean isOpen();


}
