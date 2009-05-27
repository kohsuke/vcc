package net.java.dev.vcc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class JDK5ServiceLoaderImpl<S> implements ServiceLoader<S> {

    public JDK5ServiceLoaderImpl(Class<S> service, ClassLoader loader) {
        service.getClass(); // throw NPE if null
        loader.getClass(); // throw NPE if null
        this.service = service;
        this.loader = loader;
    }

    /**
     * Loads the services of a class that are defined using the SPI mechanism.
     *
     * @param clazz       The interface / abstract class defining the service.
     * @param classLoader of type ClassLoader the classloader to use.
     * @return A list of candidate class names.
     */
    @SuppressWarnings("unchecked")
    private static List<String> loadServiceNames(Class<?> clazz, ClassLoader classLoader) {
        final Logger logger = Logger.getLogger(JDK5ServiceLoaderImpl.class.getName());

        logger.log(Level.FINEST, "loadServiceNames({0},{1})", new Object[]{clazz, classLoader});

        final String resourceName = "META-INF/services/" + clazz.getName();

        final List<String> names = new ArrayList<String>();

        try {
            final Enumeration<URL> urlEnumeration = classLoader.getResources(resourceName);

            if (urlEnumeration == null) {
                return Collections.emptyList();
            }

            if (!urlEnumeration.hasMoreElements()) {
                return Collections.emptyList();
            }

            nextUrl:
            while (urlEnumeration.hasMoreElements()) {
                final URL url = urlEnumeration.nextElement();
                try {
                    final InputStream inputStream = url.openStream();
                    try {
                        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        try {
                            final BufferedReader reader = new BufferedReader(inputStreamReader);
                            try {
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    int ci = line.indexOf('#');
                                    if (ci >= 0) {
                                        line = line.substring(0, ci);
                                    }
                                    line = line.trim();
                                    int n = line.length();
                                    if (n == 0) {
                                        continue; // next line
                                    }
                                    if ((line.indexOf(' ') >= 0) || (line.indexOf('\t') >= 0)) {
                                        logger.log(Level.WARNING, "Illegal configuration-file syntax");
                                        continue nextUrl; // next url
                                    }
                                    int cp = line.codePointAt(0);
                                    if (!Character.isJavaIdentifierStart(cp)) {
                                        logger.log(Level.SEVERE, "Illegal provider-class name: {0}", line);
                                        continue nextUrl; // next url
                                    }
                                    for (int i = Character.charCount(cp); i < n; i += Character.charCount(cp)) {
                                        cp = line.codePointAt(i);
                                        if (!Character.isJavaIdentifierPart(cp) && (cp != '.')) {
                                            logger.log(Level.SEVERE, "Illegal provider-class name: {0}", line);
                                            continue nextUrl; // next url
                                        }
                                    }
                                    if (!names.contains(line)) {
                                        names.add(line);
                                    }
                                }
                            }
                            finally {
                                reader.close();
                            }
                        }
                        finally {
                            inputStreamReader.close();
                        }
                    }
                    finally {
                        inputStream.close();
                    }
                }
                catch (IOException e) {
                    logger.log(Level.FINE, "I/O problem", e);
                }
            }
        }
        catch (IOException e) {
            logger.log(Level.WARNING, "Could not determine services", e);
        }

        return Collections.synchronizedList(names);
    }

    /**
     * The service class we are loading.
     */
    private final Class<S> service;

    /**
     * The classloader we are loading from.
     */
    private final ClassLoader loader;

    /**
     * The list of potential service names
     */
    private List<String> serviceImplNames = null;


    /**
     * {@inheritDoc}
     */
    public synchronized void reload() {
        serviceImplNames = null;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized Iterator<S> iterator() {
        if (serviceImplNames == null) {
            serviceImplNames = loadServiceNames(service, loader);
        }
        return new IteratorImpl<S>(service, loader, serviceImplNames.iterator());
    }

    /**
     * An {@link java.util.Iterator} which lazily instantiates service implementation classes.
     *
     * @param <S> The service class.
     */
    private static class IteratorImpl<S> implements Iterator<S> {
        /**
         * Keep this non-static to prevent classloader's being retained in permgen
         */
        private final Logger logger = Logger.getLogger(IteratorImpl.class.getName());

        /**
         * The service we are iterating.
         */
        private final Class<S> service;

        /**
         * The classloader to load the service from.
         */
        private final ClassLoader loader;

        /**
         * The class names which "should" be implementations of {@link #service}. Guarded by {@link #lock}.
         */
        private final Iterator<String> names;

        /**
         * A lock for advancing {@link #names} and modifing {@link #next}.
         */
        private final Object lock = new Object();

        /**
         * The next class. Note that we have to instantiate on the call to hasNext, otherwise we cannot be sure that
         * it implements the {@link #service} interface.  Guarded by {@link #lock}.
         */
        private S next = null;

        /**
         * Constructs a service iterator.
         *
         * @param service The service we want implementations of.
         * @param loader  The classloader we want implementations from.
         * @param names   The service implementation class names.
         */
        public IteratorImpl(Class<S> service, ClassLoader loader, Iterator<String> names) {
            this.service = service;
            this.loader = loader;
            this.names = names;
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        public boolean hasNext() {
            synchronized (lock) {
                if (next != null) {
                    return true;
                }
                while (names.hasNext()) {
                    String name = names.next();
                    try {
                        Class implClass = loader.loadClass(name);
                        if (!service.isAssignableFrom(implClass)) {
                            logger.log(Level.SEVERE, "{0} does not implement {1}", new Object[]{implClass, service});
                            continue;
                        }
                        next = (S) implClass.newInstance();
                        return true;
                    }
                    catch (ClassNotFoundException e) {
                        logger.log(Level.SEVERE, "Could not find provider", e);
                    }
                    catch (IllegalAccessException e) {
                        logger.log(Level.WARNING, "Could not access provider", e);
                    }
                    catch (InstantiationException e) {
                        logger.log(Level.WARNING, "Could not instantiate provider", e);
                    }
                    try {
                        names.remove();
                    } catch (UnsupportedOperationException e) {
                        // ahh well we tried removing to save future iterators from trying to instantiate it again
                        // nevermind
                    }
                }
                return false;
            }
        }

        /**
         * {@inheritDoc}
         */
        public S next() {
            synchronized (lock) {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                try {
                    return next;
                } finally {
                    next = null;
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
