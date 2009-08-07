package net.java.dev.vcc.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * A proxy for {@link net.java.dev.vcc.util.ServiceLoader} that uses either the native Java 6 service loader
 * implementation or an internal implementation that works on Java 5.
 */
public class ServiceLoaderProxy<S> implements ServiceLoader<S> {

    /**
     * The real {@link net.java.dev.vcc.util.ServiceLoader}
     */
    private final ServiceLoader<S> delegate;

    /**
     * Creates a new {@link net.java.dev.vcc.util.ServiceLoader} instance that proxies for the specified delegate.
     *
     * @param delegate The real {@link net.java.dev.vcc.util.ServiceLoader}
     */
    private ServiceLoaderProxy(ServiceLoader<S> delegate) {
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    public void reload() {
        delegate.reload();
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<S> iterator() {
        return delegate.iterator();
    }

    /**
     * Creates a new service loader for the given service type and class loader.
     *
     * @param service The interface or abstract class representing the service
     * @param loader  The class loader to be used to load provider-configuration files and provider classes, or
     *                <tt>null</tt> if the system class loader (or, failing that, the bootstrap class loader) is to be
     *                used
     *
     * @return A new service loader
     */
    @SuppressWarnings("unchecked")
    public static <S> ServiceLoaderProxy<S> load(Class<S> service, ClassLoader loader) {
        final Logger logger = Logger.getLogger(ServiceLoaderProxy.class.getName());
        try {
            final Constructor<? extends ServiceLoader> constructor =
                    AdapterProvider.provider.getConstructor(Class.class, ClassLoader.class);
            return new ServiceLoaderProxy<S>(constructor.newInstance(service, loader));
        }
        catch (NoSuchMethodException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
        catch (InvocationTargetException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
        catch (IllegalAccessException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
        catch (InstantiationException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
        // just fall back to the one we know works...
        return new ServiceLoaderProxy<S>(new JDK5ServiceLoaderImpl<S>(service, loader));
    }

    /**
     * Method getContextClassLoader returns the contextClassLoader of the current thread.
     *
     * @return the contextClassLoader (type ClassLoader) of the current thread.
     */
    @SuppressWarnings("unchecked")
    public static ClassLoader getContextClassLoader() {
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

    /**
     * Thread safe lazy resource singleton initialization.
     */
    private static class AdapterProvider {
        private static final Class<? extends ServiceLoader> provider = findProvider();

        @SuppressWarnings("unchecked")
        private static Class<? extends ServiceLoader> findProvider() {
            // first try JDK 6
            try {
                // see if
                Class.forName("java.util.ServiceLoader", false, ClassLoader.getSystemClassLoader());
                return (Class<? extends ServiceLoader>) ServiceLoaderProxy.class.getClassLoader().loadClass(
                        ServiceLoaderProxy.class.getPackage().getName() + ".JDK6ServiceLoaderImpl");
            } catch (ClassNotFoundException e) {
                // expected if we are not on JDK 6
                return JDK5ServiceLoaderImpl.class;
            } catch (SecurityException e) {
                // might also occur if we are not on JDK 6... system classloader may complain about trying to
                // load a java.util class... seemingly even before it's checked to see if the class is in
                // the system class loader... never mind that we are specifically asking the system class loader!
                return JDK5ServiceLoaderImpl.class;
            }
        }
    }
}
