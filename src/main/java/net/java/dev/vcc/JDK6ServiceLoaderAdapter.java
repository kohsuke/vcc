package net.java.dev.vcc;

import java.util.Iterator;
import java.util.ServiceLoader;

class JDK6ServiceLoaderAdapter<S> implements Adapter<S> {

    private final ServiceLoader<S> delegate;

    public JDK6ServiceLoaderAdapter(Class<S> service, ClassLoader loader) {
        this.delegate = ServiceLoader.load(service, loader);
    }

    public void reload() {
        delegate.reload();
    }

    public Iterator<S> iterator() {
        return delegate.iterator();
    }
}
