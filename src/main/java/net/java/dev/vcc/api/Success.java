package net.java.dev.vcc.api;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * A class used to signify when a {@link net.java.dev.vcc.api.Command} either returns success or throws an exception.
 */
public final class Success implements Serializable {
    /**
     * Provide for consistent serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Lazy resource initialization class.
     */
    private static class ResourceHolder {
        private static final Success INSTANCE = new Success();
    }

    /**
     * Do not instantiate directly.
     */
    private Success() {
        // only one instance allowed.
    }

    /**
     * Returns the Success singleton.
     * @return the Success singleton.
     */
    public static Success getInstance() {
        return ResourceHolder.INSTANCE;
    }

    /**
     * Throws CloneNotSupportedException.  This guarantees that enums
     * are never cloned, which is necessary to preserve their "singleton"
     * status.
     *
     * @return (neverreturns)
     */
    protected final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    /**
     * Ensure that we only ever have one instance.
     * @return The singleton instance.
     * @throws ObjectStreamException if it sees any self-propelled airborne ham or pork.
     */
    private Object readResolve() throws ObjectStreamException {
        return ResourceHolder.INSTANCE;
    }


}
