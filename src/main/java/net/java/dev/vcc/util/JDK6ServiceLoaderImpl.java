package net.java.dev.vcc.util;

import java.util.Iterator;

class JDK6ServiceLoaderImpl<S>
        implements ServiceLoader<S> {

    /**
     * The {@link java.util.ServiceLoader} we are using.
     */
    private final java.util.ServiceLoader delegate;

    public JDK6ServiceLoaderImpl(Class<S> service, ClassLoader loader) {
        service.getClass(); // throw NPE if null
        loader.getClass(); // throw NPE if null
        this.delegate = java.util.ServiceLoader.load(service, loader);
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
    @SuppressWarnings("unchecked")
    public Iterator<S> iterator() {
        return delegate.iterator();
    }
}
