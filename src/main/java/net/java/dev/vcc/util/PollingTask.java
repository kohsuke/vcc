package net.java.dev.vcc.util;

import java.util.concurrent.TimeUnit;

/**
 * A task that will repeatedly poll.
 */
public abstract class PollingTask
    implements Runnable
{
    /**
     * The task we want to repeatedly execute.
     */
    private final Runnable pollTask;

    /**
     * A lock for our polling task statistics.
     */
    private final Object averageLock = new Object();

    /**
     * Recent average of the polling task duration in seconds.
     */
    private double fastAverage = 1.0;

    /**
     * Long term average of the polling task duration in seconds.
     */
    private double slowAverage = 1.0;

    /**
     * The decay rate for the "fast" average.
     */
    private static final double FAST_RATE = 0.1;

    /**
     * The decay rate for the "slow" average.
     */
    private static final double SLOW_RATE = 0.001;

    /**
     * The length of a nanosecond in seconds.
     */
    protected static final double TO_SECONDS = 1.0 / TimeUnit.SECONDS.toNanos( 1 );

    /**
     * Creates a new {@link PollingTask}
     * @param pollTask the task to run.
     */
    protected PollingTask( Runnable pollTask )
    {
        this.pollTask = pollTask;
    }

    /**
     * Performs the polling task.  Any exceptions thrown by the polling task will be quashed.
     * The average times will be updated.
     */
    protected void poll()
    {
        final long pollStart = System.nanoTime();
        try
        {
            pollTask.run();
        }
        catch ( Throwable t )
        {
            // ignore
        }
        final long pollEnd = System.nanoTime();
        final double pollDuration = (pollEnd - pollStart) * TO_SECONDS;
        if ( pollDuration < 0 )
        {
            // clock roll-over
            // ignore for stats
        }
        else
        {
            synchronized ( averageLock )
            {
                fastAverage = fastAverage * ( 1 - FAST_RATE ) + pollDuration * FAST_RATE;
                slowAverage = slowAverage * ( 1 - SLOW_RATE ) + pollDuration * SLOW_RATE;
            }
        }
    }

    /**
     * Gets the "fast" average time a poll takes to complete. The "fast" average is more sensitive
     * to recent trends.
     *  
     * @return the "fast" average time a poll takes to complete.
     */
    public double getFastAverage()
    {
        synchronized ( averageLock ) {
        return fastAverage;
        }
    }

    /**
     * Gets the "slow" average time a poll takes to complete. The "slow" average is a long time average
     * and will be insensitive to recent spikes.
     *  
     * @return the "slow" average time a poll takes to complete.
     */
    public double getSlowAverage()
    {
        synchronized ( averageLock ) {
            return slowAverage;
        }
    }
    
    public boolean isRunning() {
        return true;
    }
}
