package net.java.dev.vcc.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Iterator;

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

    public static <S> ServiceLoaderProxy<S> load(Class<S> service, ClassLoader loader) {
        try {
            final Constructor<? extends ServiceLoader> constructor =
                    AdapterProvider.provider.getConstructor(Class.class, ClassLoader.class);
            return new ServiceLoaderProxy<S>(constructor.newInstance(service, loader));
        }
        catch (NoSuchMethodException e) {
        }
        catch (InvocationTargetException e) {
        }
        catch (IllegalAccessException e) {
        }
        catch (InstantiationException e) {
        }
        return new ServiceLoaderProxy<S>(new ServiceLoader<S>() {
            public void reload() {
            }

            public Iterator<S> iterator() {
                return Collections.<S>emptySet().iterator();
            }
        });
    }

    private static class AdapterProvider {
        private static final Class<? extends ServiceLoader> provider = findProvider();

        @SuppressWarnings("unchecked")
        private static Class<? extends ServiceLoader> findProvider() {
            // first try JDK6
            try {
                Class.forName("java.util.ServiceLoader");
                return (Class<? extends ServiceLoader>) ServiceLoaderProxy.class.getClassLoader().loadClass(
                        ServiceLoaderProxy.class.getPackage().getName() + ".JDK6ServiceLoaderImpl");
            }
            catch (ClassNotFoundException e) {
                return JDK5ServiceLoaderImpl.class;
            }
        }
    }
}
