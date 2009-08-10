package net.java.dev.vcc.util;

import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

/**
 * A reference to a future value.
 */
public class FutureReference<T> implements Future<T> {
    private boolean done = false;
    private T value;
    private String message;
    private Throwable cause;
    private final Lock referenceLock = new ReentrantLock();
    private final Condition completed = referenceLock.newCondition();

    /**
     * {@inheritDoc}
     */
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;  // We have no way to cancel.
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCancelled() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDone() {
        referenceLock.lock();
        try {
            return done;
        } finally {
            referenceLock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    public T get() throws InterruptedException, ExecutionException {
        referenceLock.lock();
        try {
            while (!done) {
                completed.await();
            }
            if (cause != null) {
                throw new ExecutionException(message, cause);
            }
            return value;
        } finally {
            referenceLock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        referenceLock.lock();
        try {
            long nanosTimeout = unit.toNanos(timeout);
            while (!done) {
                if (nanosTimeout > 0) {
                    nanosTimeout = completed.awaitNanos(nanosTimeout);
                } else {
                    throw new TimeoutException();
                }
            }
            if (cause != null) {
                throw new ExecutionException(message, cause);
            }
            return value;
        } finally {
            referenceLock.unlock();
        }
    }

    /**
     * Sets the value and completes the future.
     *
     * @param value the completed value.
     */
    public void set(T value) {
        referenceLock.lock();
        try {
            if (done) {
                throw new IllegalStateException("Already completed");
            }
            done = true;
            this.value = value;
            completed.signalAll();
        } finally {
            referenceLock.unlock();
        }
    }

    /**
     * Sets the value and completes the future as an exception.
     *
     * @param message the message.
     * @param cause   the cause.
     */
    public void set(String message, Throwable cause) {
        referenceLock.lock();
        try {
            if (done) {
                throw new IllegalStateException("Already completed");
            }
            done = true;
            this.message = message;
            this.cause = cause;
            completed.signalAll();
        } finally {
            referenceLock.unlock();
        }
    }
}
