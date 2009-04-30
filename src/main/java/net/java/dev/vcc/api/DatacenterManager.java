package net.java.dev.vcc.api;

import net.java.dev.vcc.spi.DatacenterConnection;
import net.java.dev.vcc.util.ServiceLoaderProxy;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;

/**
 * The connection factory creates connections with which to control Virtual Computers and their Hosts.
 */
public final class DatacenterManager
{

    /**
     * The service loader we're using
     *
     * @todo refactor so that we are lazy in getting this, and return the correct one for the calling classloader.
     */
    private static final ServiceLoaderProxy<DatacenterConnection> serviceLoader =
        ServiceLoaderProxy.load( DatacenterConnection.class, getContextClassLoader() );

    /**
     * Do not instantiate
     */
    private DatacenterManager()
    {
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
    public static Datacenter getConnection( String url, String username, char[] password )
    {
        Iterator<DatacenterConnection> i =
            ServiceLoaderProxy.load( DatacenterConnection.class, getContextClassLoader() ).iterator();
        while ( i.hasNext() )
        {
            DatacenterConnection manager = i.next();
            if ( manager.acceptsUrl( url ) )
            {
                return manager.connect( url, username, password );
            }
        }
        throw new RuntimeException( "give this a real exception" ); // TODO
    }


    /**
     * Method getContextClassLoader returns the contextClassLoader of the current thread.
     *
     * @return the contextClassLoader (type ClassLoader) of the current thread.
     */
    @SuppressWarnings("unchecked")
    private static ClassLoader getContextClassLoader()
    {
        return (ClassLoader) AccessController.doPrivileged( new PrivilegedAction()
        {
            /** {@inheritDoc} */
            public Object run()
            {
                ClassLoader cl = null;
                //try {
                cl = Thread.currentThread().getContextClassLoader();
                //} catch (SecurityException ex) { }

                if ( cl == null )
                {
                    cl = ClassLoader.getSystemClassLoader();
                }

                return cl;
            }
        } );
    }

}
