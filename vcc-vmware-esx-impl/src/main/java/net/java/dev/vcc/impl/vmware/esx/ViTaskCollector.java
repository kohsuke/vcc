package net.java.dev.vcc.impl.vmware.esx;

import com.vmware.vim25.InvalidStateFaultMsg;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.TaskFilterSpec;
import com.vmware.vim25.TaskInfo;
import com.vmware.vim25.TaskInfoState;
import net.java.dev.vcc.api.Log;
import net.java.dev.vcc.api.LogFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Collects events and queues them up for later processing.
 */
final class ViTaskCollector implements Runnable {

    private final ManagedObjectReference taskCollector;

    private ViDatacenter viDatacenter;
    private final Log log;

    public ViTaskCollector(ViDatacenter viDatacenter, LogFactory logFactory)
            throws RuntimeFaultFaultMsg, InvalidStateFaultMsg {
        this.viDatacenter = viDatacenter;
        this.log = logFactory.getLog(getClass());

        TaskFilterSpec spec = new TaskFilterSpec();
        spec.setState(Arrays.asList(TaskInfoState.SUCCESS, TaskInfoState.ERROR));
        this.taskCollector =
                viDatacenter.getConnection().getProxy()
                        .createCollectorForTasks(viDatacenter.getConnection().getServiceContent().getTaskManager(),
                                spec);
        viDatacenter.getConnection().getProxy().resetCollector(taskCollector);
    }

    public void run() {
        log.debug("Starting collecting tasks");
        try {
            boolean finished = false;
            while (!viDatacenter.isClosing() && !finished) {
                List<TaskInfo> tasks;
                try {
                    tasks = viDatacenter.getConnection().getProxy().readNextTasks(taskCollector, 100);
                }
                catch (RuntimeFaultFaultMsg e) {
                    log.error(e, e.getMessage());
                    return;
                }
                for (TaskInfo task : tasks) {
                    viDatacenter.processTask(task);
                }
            }
        } finally {
            log.debug("Finished collecting tasks.");
        }
    }

}