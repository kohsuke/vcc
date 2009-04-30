package net.java.dev.vcc.util;

import java.util.Iterator;

class JDK6ServiceLoaderImpl<S>
        implements ServiceLoader<S> {

    private final java.util.ServiceLoader delegate;

    public JDK6ServiceLoaderImpl(Class<S> service, ClassLoader loader) {
        this.delegate = java.util.ServiceLoader.load(service, loader);
    }

    public void reload() {
        delegate.reload();
    }

    public Iterator<S> iterator() {
        return delegate.iterator();
    }
}
