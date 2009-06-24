package net.java.dev.vcc.util;

import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: user
 * Date: 24-Jun-2009
 * Time: 19:08:22
 * To change this template use File | Settings | File Templates.
 */
public class DefaultPollingTask
    extends PollingTask
{
    private final double frequency;

    /**
     * Creates a new {@link PollingTask}
     *
     * @param pollTask  the task to run.
     * @param frequency
     */
    protected DefaultPollingTask( Runnable pollTask, long frequency, TimeUnit frequencyUnit )
    {
        super( pollTask );
        this.frequency = frequencyUnit.toNanos( frequency ) * TO_SECONDS;
    }

    /** {@inheritDoc} */
    public void run()
    {
        while ( isRunning() ) {
            poll();
            try
            {
                // sleep for the frequency less the average poll duration
                // so that on average polls will complete with the desired frequency
                TimeUnit.NANOSECONDS.sleep( (long) (Math.max( frequency - getSlowAverage(), 0 ) / TO_SECONDS));
            }
            catch ( InterruptedException e )
            {
                // ignore
            }
        }
    }
}
