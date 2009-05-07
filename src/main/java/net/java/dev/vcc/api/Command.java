package net.java.dev.vcc.api;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Represents an operation to be performed. All instances of this class must comply with the JavaBeans specification 
 * with the following modification: Setters will throw an {@link IllegalStateException} if the command has been 
 * submitted, i.e. a setFoo method would look like this<pre>
 * public void setFoo(Foo foo) {
 *     checkNotSubmitted();
 *     this.foo = foo;
 * }
 * </pre>
 * <p/>
 * When a {@link Command} subclass is being extended, any new parameters should have 
 * default values which ensure that older clients which are unaware of the new parameters will still achieve the same
 * behaviour.
 *
 * @param <RESULT> the type of result from this command.  If the command does not return a result per-se, but can either
 * succeed or fail, then the result type should be {@link Success} and failure will be
 * indicated by throwing an exception.
 */
public abstract class Command<RESULT> implements Future<RESULT> {

    /**
     * The internal lock object.
     */
    private final Object internalLock = new Object();

    /**
     * The future that this command is acting as a proxy for.
     * Guarded by {@link #internalLock}.
     */
    private Future<RESULT> delegate = null;

    /**
     * Throws an {@link IllegalStateException} if this command has been submitted.
     *
     * @throws IllegalStateException if this command has been submitted.
     */
    protected final void checkNotSubmitted() {
        synchronized (internalLock) {
            if (this.delegate != null) {
                throw new IllegalStateException("This command has already been submitted");
            }
        }
    }

    /**
     * Throws an {@link IllegalStateException} if this command has not been submitted.
     *
     * @throws IllegalStateException if this command has not been submitted.
     */
    protected final void checkSubmitted() {
        synchronized (internalLock) {
            if (this.delegate == null) {
                throw new IllegalStateException("This command has not been submitted yet");
            }
        }
    }

    /**
     * This method is called by an SPI when the command has been submitted.
     * @param delegate The future representing the command.
     */
    public final void setSubmitted(Future<RESULT> delegate) {
        delegate.getClass(); // throw NPE if null
        synchronized (internalLock) {
            checkNotSubmitted();
            this.delegate = delegate;
        }
    }

    /**
     * {@inheritDoc}
     */
    public final boolean cancel(boolean mayInterruptIfRunning) {
        synchronized (internalLock) {
            checkSubmitted();
            return delegate.cancel(mayInterruptIfRunning);
        }
    }

    /**
     * Returns {@code true} if the command has been submitted.
     * @return {@code true} if the command has been submitted.
     */
    public final boolean isSubmitted() {
        synchronized (internalLock) {
            return this.delegate != null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public final boolean isCancelled() {
        synchronized (internalLock) {
            return this.delegate != null && this.delegate.isCancelled();
        }
    }

    /**
     * {@inheritDoc}
     */
    public final boolean isDone() {
        synchronized (internalLock) {
            return this.delegate != null && this.delegate.isDone();
        }
    }

    /**
     * {@inheritDoc}
     */
    public final RESULT get() throws InterruptedException, ExecutionException {
        synchronized (internalLock) {
            checkSubmitted();
            return delegate.get();
        }
    }

    /**
     * {@inheritDoc}
     */
    public final RESULT get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        synchronized (internalLock) {
            checkSubmitted();
            return delegate.get(timeout, unit);
        }
    }
}
