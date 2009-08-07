package net.java.dev.vcc.spi;

import net.java.dev.vcc.api.LogFactory;
import net.java.dev.vcc.api.Log;
import net.java.dev.vcc.util.ServiceLoaderProxy;

import java.util.Map;
import java.util.WeakHashMap;
import java.lang.ref.WeakReference;

/**
 * Base class to for implementors of {@link net.java.dev.vcc.api.LogFactory}
 */
public abstract class AbstractLogFactory implements LogFactory {

    /**
     * A weak cache of {@link Log}s that should allow unloading without a permgen leak (I hope).
     * Guarded by itself.
     */
    private final Map<String, WeakReference<Log>> logs = new WeakHashMap<String, WeakReference<Log>>();

    /**
     * {@inheritDoc}
     */
    public final Log getLog(Class clazz) {
        return getLog(clazz.getName(), null);
    }

    /**
     * {@inheritDoc}
     */
    public final Log getLog(String name) {
        return getLog(name, null);
    }

    /**
     * {@inheritDoc}
     */
    public final Log getLog(Class clazz, String bundleName) {
        return getLog(clazz.getName(), null);
    }

    /**
     * {@inheritDoc}
     */
    public final Log getLog(String name, String bundleName) {
        final String key = (name == null ? "null" : name) + ":" + (bundleName == null ? "null" : bundleName);
        synchronized (logs) {
            WeakReference<Log> ref = logs.get(key);
            if (ref != null) {
                final Log result = ref.get();
                if (result != null) {
                    return result;
                }
            }
            final Log result = newLog(name, bundleName);
            logs.put(key, new WeakReference<Log>(result));
            return result;
        }
    }

    /**
     * Default constructor.
     */
    protected AbstractLogFactory() {
    }

    /**
     * Creates the named {@link Log} instance.
     * @param name The name of the {@link Log} to create.
     * @return The {@link Log}.
     */
    protected abstract Log newLog(String name, String bundleName);
}
