package net.java.dev.vcc.util;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: user
 * Date: 06-May-2009
 * Time: 18:13:01
 * To change this template use File | Settings | File Templates.
 */
public final class ServiceLoaderCache<T> {

    private final Class<T> serviceClass;

    /**
     * A weak cache of service loader proxies that should allow unloading without a permgen leak (I hope).
     * Guarded by itself.
     */
    private final Map<ClassLoader, WeakReference<ServiceLoaderProxy<T>>> serviceLoaderCache =
            new WeakHashMap<ClassLoader, WeakReference<ServiceLoaderProxy<T>>>();

    public ServiceLoaderCache(Class<T> serviceClass) {
        this.serviceClass = serviceClass;
    }

    /**
     * Gets the {@link ServiceLoaderProxy} from the weak cache, or creates a new one if needed.
     *
     * @param classloader The classloader we are to get the {@link ServiceLoaderProxy} from.
     * @return The {@link ServiceLoaderProxy} for the specified classloader.
     */
    public final ServiceLoaderProxy<T> get(ClassLoader classloader) {
        synchronized (serviceLoaderCache) {
            WeakReference<ServiceLoaderProxy<T>> ref = serviceLoaderCache.get(classloader);
            if (ref != null) {
                final ServiceLoaderProxy<T> result = ref.get();
                if (result != null) {
                    return result;
                }
            }
            final ServiceLoaderProxy<T> result =
                    ServiceLoaderProxy.load(serviceClass, classloader);
            serviceLoaderCache.put(classloader, new WeakReference<ServiceLoaderProxy<T>>(result));
            return result;
        }
    }


}
