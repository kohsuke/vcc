package net.java.dev.vcc.util;

import java.util.concurrent.TimeUnit;

/**
 * Used by tasks to determine if they should continue execution.
 */
public interface TaskController {
    /**
     * Returns {@code true} while the task can continue running.
     *
     * @return {@code true} while the task can continue running.
     */
    boolean isActive();

    /**
     * Blocks the calling thread until the task controller has been deactivated or the calling thread is interrupted.
     *
     * @throws InterruptedException if the calling thread was interrupted.
     */
    void awaitDeactivated() throws InterruptedException;

    /**
     * Blocks the calling thread until the task controller has been deactivated, the timeout has expired  or the calling
     * thread is interrupted.
     *
     * @param timeout The timeout.
     * @param unit    The units in which the timeout is expressed.
     *
     * @return {@code true} if the task controller has been deactivated.
     *
     * @throws InterruptedException if the calling thread was interrupted.
     */
    boolean awaitDeactivated(long timeout, TimeUnit unit) throws InterruptedException;

}
