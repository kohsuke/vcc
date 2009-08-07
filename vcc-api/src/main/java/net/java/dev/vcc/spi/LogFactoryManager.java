package net.java.dev.vcc.spi;

import net.java.dev.vcc.util.ServiceLoaderCache;
import net.java.dev.vcc.util.ServiceLoaderProxy;
import net.java.dev.vcc.api.Log;
import net.java.dev.vcc.api.LogFactory;

import java.util.Iterator;

/**
 * Provides {@link Log} .
 */
public class LogFactoryManager {
    /**
     * A weak cache of service loader proxies that should allow unloading without a permgen leak (I hope). Guarded by
     * itself.
     */
    private static final ServiceLoaderCache<LogFactory> cache = new ServiceLoaderCache<LogFactory>(LogFactory.class);

    /**
     * Do not instantiate
     */
    private LogFactoryManager() {
    }

    /**
     * Gets a log factory.
     *
     * @return The log factory.
     */
    public static LogFactory getLogFactory() {
        return getLogFactory(ServiceLoaderProxy.getContextClassLoader());
    }

    /**
     * Gets a log factory.
     *
     * @param classLoader The classloader to search for implementations from.
     *
     * @return The log factory.
     *
     * @throws RuntimeException until we get our own exception for when we cannot get a connection.
     */
    public static LogFactory getLogFactory(ClassLoader classLoader) {
        Iterator<LogFactory> i = cache.get(classLoader).iterator();
        if (i.hasNext()) {
            return i.next();
        }
        return new DefaultLogFactory();
    }


}
