package net.java.dev.vcc.test;

import net.java.dev.vcc.Connection;
import net.java.dev.vcc.Host;
import net.java.dev.vcc.Computer;
import net.java.dev.vcc.PowerState;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: user
 * Date: 17-Sep-2008
 * Time: 18:43:57
 * To change this template use File | Settings | File Templates.
 */
public class CrappyConnection implements Connection {
    public Set<Host> getHosts() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Set<Computer> getComputers() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Set<PowerState> getAllowedStates() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Set<PowerState> getAllowedStates(PowerState from) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void close() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isOpen() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
