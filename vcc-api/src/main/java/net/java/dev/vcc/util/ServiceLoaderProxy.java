package net.java.dev.vcc.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: user
 * Date: 17-Sep-2008
 * Time: 08:08:32
 * To change this template use File | Settings | File Templates.
 */
public class ServiceLoaderProxy<S>
        implements ServiceLoader<S> {
    private final ServiceLoader<S> delegate;

    private ServiceLoaderProxy(ServiceLoader<S> delegate) {
        this.delegate = delegate;
    }

    public void reload() {
        delegate.reload();
    }

    public Iterator<S> iterator() {
        return delegate.iterator();
    }

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
