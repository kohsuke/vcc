package net.java.dev.vcc.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * A {@link Future} that has already been completed.
 */
public final class CompletedFuture<T> implements Future<T> {
    private final T value;
    private final String message;
    private final Throwable cause;

    /**
     * Creates a {@link java.util.concurrent.Future} which has completed successfully.
     *
     * @param value The value of the future.
     */
    public CompletedFuture(T value) {
        this.value = value;
        this.message = null;
        this.cause = null;
    }

    /**
     * Creates a {@link java.util.concurrent.Future} which has completed by throwing an exception.
     *
     * @param message The message to put in the {@link ExecutionException}.
     * @param cause   The execption which was thrown.
     */
    public CompletedFuture(String message, Throwable cause) {
        this.message = message;
        this.cause = cause;
        this.value = null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
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
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public T get() throws ExecutionException {
        if (cause == null) {
            return value;
        }
        throw new ExecutionException(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public T get(long timeout, TimeUnit unit) throws ExecutionException {
        return get();
    }
}
