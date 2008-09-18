package net.java.dev.vcc;

import java.util.Set;
import java.util.concurrent.Future;

/**
 * Created by IntelliJ IDEA.
 * User: user
 * Date: 18-Sep-2008
 * Time: 08:26:56
 * To change this template use File | Settings | File Templates.
 */
public interface Computer {

    Host getHost();

    ComputerState getState();

    Set<ComputerSnapshot> getSnapshots();

    Set<ComputerState> getAllowedStates();

    Set<Host> getAllowedHosts();

    String getName();

    Future<Boolean> doSetHost(Host destination);

    Future<Boolean> doSetState(ComputerState state);

    Future<ComputerState> doPowerOn();

    Future<ComputerState> doPowerOff(boolean hard);

    Future<ComputerState> doSuspend();

    Future<ComputerState> doResume();

    Future<ComputerSnapshot> doTakeSnapshot(String suggestedName, String suggestedDescription);

    Future<Boolean> doRevertToSnapshot(ComputerSnapshot snapshot);
}
