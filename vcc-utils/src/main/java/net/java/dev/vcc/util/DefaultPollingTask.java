package net.java.dev.vcc.util;

import java.util.concurrent.TimeUnit;

/**
 * A default {@link net.java.dev.vcc.util.PollingTask} implementation that tries to run the task at (on average) a
 * steady interval.
 */
public class DefaultPollingTask
        extends PollingTask {
    private final double interval;

    /**
     * Creates a new {@link PollingTask}
     *
     * @param pollTask the task to run.
     * @param interval
     */
    public DefaultPollingTask(TaskController controller, Runnable pollTask, long interval, TimeUnit intervalUnit) {
        super(controller, pollTask);
        this.interval = intervalUnit.toNanos(interval) * TO_SECONDS;
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        while (getController().isActive()) {
            double last = poll();
            try {
                // sleep for the interval less the average poll duration
                // so that on average polls will complete with the desired interval
                TimeUnit.NANOSECONDS.sleep((long) (Math.max(interval
                        - Math.min(Math.max(getSlowAverage(), getFastAverage()), last), 0) / TO_SECONDS));
            }
            catch (InterruptedException e) {
                // ignore
            }
        }
    }
}
