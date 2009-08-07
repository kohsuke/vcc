package net.java.dev.vcc.api;

import net.java.dev.vcc.spi.DatacenterConnection;
import net.java.dev.vcc.spi.LogFactoryManager;
import net.java.dev.vcc.util.ServiceLoaderCache;
import net.java.dev.vcc.util.ServiceLoaderProxy;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.LogManager;

/**
 * The connection factory creates connections with which to control Virtual Computers and their Hosts.
 */
public final class DatacenterManager {

    /**
     * A weak cache of service loader proxies that should allow unloading without a permgen leak (I hope).
     * Guarded by itself.
     */
    private static final ServiceLoaderCache<DatacenterConnection> cache =
            new ServiceLoaderCache<DatacenterConnection>(DatacenterConnection.class);

    /**
     * Do not instantiate
     */
    private DatacenterManager() {
    }

    /**
     * Gets a connection.
     *
     * @param url      The URL of the connection to get.
     * @param username The username to authenticate with (may be {@code null} if the {@link net.java.dev.vcc.spi.DatacenterConnection} does not
     *                 require quthenticaction.
     * @param password The password to authenticate with (may be {@code null} if the {@link net.java.dev.vcc.spi.DatacenterConnection} does not
     *                 require quthenticaction. (Note a char array is used in order to prevent the String of the password getting
     *                 Interned.  The char array can be overwritten once it has been used to prevent memory probes from sniffing the
     *                 password in clear text)
     * @return The connection.
     * @throws RuntimeException until we get our own exception for when we cannot get a connection.
     */
    public static Datacenter getConnection(String url, String username, char[] password) throws IOException {
        return getConnection(ServiceLoaderProxy.getContextClassLoader(), url, username, password);
    }

    /**
     * Gets a connection.
     *
     * @param classLoader The classloader to search for implementations from.
     * @param url         The URL of the connection to get.
     * @param username    The username to authenticate with (may be {@code null} if the {@link net.java.dev.vcc.spi.DatacenterConnection} does not
     *                    require quthenticaction.
     * @param password    The password to authenticate with (may be {@code null} if the {@link net.java.dev.vcc.spi.DatacenterConnection} does not
     *                    require quthenticaction. (Note a char array is used in order to prevent the String of the password getting
     *                    Interned.  The char array can be overwritten once it has been used to prevent memory probes from sniffing the
     *                    password in clear text)
     * @return The connection.
     * @throws RuntimeException until we get our own exception for when we cannot get a connection.
     */
    public static Datacenter getConnection(ClassLoader classLoader, String url, String username, char[] password) throws IOException {
        Iterator<DatacenterConnection> i = cache.get(classLoader).iterator();
        while (i.hasNext()) {
            DatacenterConnection manager = i.next();
            if (manager.acceptsUrl(url)) {
                return manager.connect(url, username, password, LogFactoryManager.getLogFactory(classLoader));
            }
        }
        throw new RuntimeException("give this a real exception"); // TODO
    }

}
