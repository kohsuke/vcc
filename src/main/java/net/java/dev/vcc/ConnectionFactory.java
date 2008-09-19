package net.java.dev.vcc;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;

public final class ConnectionFactory {

    private static final ServiceLoaderProxy<ConnectionProvider> serviceLoader = ServiceLoaderProxy.load(ConnectionProvider.class, getContextClassLoader());


    /**
     * Do not instantiate
     */
    private ConnectionFactory() {
    }

    public static Connection getConnection(String url, String username, char[] password) {
        Iterator<ConnectionProvider> i = ServiceLoaderProxy.load(ConnectionProvider.class, getContextClassLoader()).iterator();
        while (i.hasNext()) {
            ConnectionProvider manager = i.next();
            if (manager.acceptsUrl(url)) {
                return manager.connect(url, username, password);
            }
        }
        throw new RuntimeException("give this a real exception"); // TODO
    }


    /**
     * Method getContextClassLoader returns the contextClassLoader of the current thread.
     *
     * @return the contextClassLoader (type ClassLoader) of the current thread.
     */
    @SuppressWarnings("unchecked")
    private static ClassLoader getContextClassLoader() {
        return (ClassLoader) AccessController.doPrivileged(new PrivilegedAction() {
            /** {@inheritDoc} */
            public Object run() {
                ClassLoader cl = null;
                //try {
                cl = Thread.currentThread().getContextClassLoader();
                //} catch (SecurityException ex) { }

                if (cl == null) {
                    cl = ClassLoader.getSystemClassLoader();
                }

                return cl;
            }
        });
    }

}
