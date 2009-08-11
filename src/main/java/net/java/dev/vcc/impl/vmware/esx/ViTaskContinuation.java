package net.java.dev.vcc.impl.vmware.esx;

import com.vmware.vim25.LocalizedMethodFault;
import net.java.dev.vcc.util.FutureReference;

import java.util.concurrent.Future;

/**
 * Created by IntelliJ IDEA. User: connollys Date: Aug 11, 2009 Time: 9:17:30 AM To change this template use File |
 * Settings | File Templates.
 */
public abstract class ViTaskContinuation<T> {
    private final FutureReference<T> value = new FutureReference<T>();

    public final Future<T> getFuture() {
        return value;
    }

    protected final void set(T value) {
        this.value.set(value);
    }

    protected final void set(String message, Throwable t) {
        this.value.set(message, t);
    }

    public abstract void onSuccess();

    public abstract void onError(LocalizedMethodFault error);
}
