package net.java.dev.vcc.impl.vmware.esx;

import net.java.dev.vcc.api.LogFactory;
import net.java.dev.vcc.spi.AbstractDatacenter;
import net.java.dev.vcc.spi.DatacenterConnection;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * A {@link net.java.dev.vcc.spi.DatacenterConnection} for VMware ESX.
 */
public class ViDatacenterConnection implements DatacenterConnection {
    public boolean acceptsUrl(String url) {
        return url.startsWith("vcc+vi+http://") || url.startsWith("vcc+vi+https://");
    }

    public AbstractDatacenter connect(String url, String username, char[] password, LogFactory logFactory)
            throws IOException {
        assert url.startsWith("vcc+vi+");
        logFactory.getClass();
        try {
            ExecutorService executorService = Executors.newCachedThreadPool(new ViThreadFactory());
            ViConnection connection = new ViConnection(url.substring("vcc+vi+".length()), username, password,
                    executorService);
            return new ViDatacenter(new ViDatacenterId(url), connection, logFactory, executorService);
        } catch (Exception e) {
            IOException ioe = new IOException(e.getMessage());
            ioe.initCause(e);
            throw ioe;
        }
    }

    private static class ViThreadFactory
            implements ThreadFactory {
        private final ThreadFactory delegate = Executors.defaultThreadFactory();

        public Thread newThread(Runnable r) {
            Thread result = delegate.newThread(r);
            result.setName("VMwareESX-" + result.getName());
            return result;
        }
    }
}
