package net.java.dev.vcc.impl.vmware.esx;

import net.java.dev.vcc.spi.AbstractDatacenter;
import net.java.dev.vcc.spi.DatacenterConnection;
import net.java.dev.vcc.api.LogFactory;

import java.io.IOException;

/**
 * A {@link net.java.dev.vcc.spi.DatacenterConnection} for VMware ESX.
 */
public class ViDatacenterConnection implements DatacenterConnection {
    public boolean acceptsUrl(String url) {
        return url.startsWith("vcc+vi+http://") || url.startsWith("vcc+vi+https://");
    }

    public AbstractDatacenter connect(String url, String username, char[] password, LogFactory logFactory) throws IOException {
        assert url.startsWith("vcc+vi+");
        logFactory.getClass();
        try {
            ViConnection connection = new ViConnection(url.substring("vcc+vi+".length()), username, password);
            return new ViDatacenter(new ViDatacenterId(url), connection, logFactory);
        } catch (Exception e) {
            IOException ioe = new IOException(e.getMessage());
            ioe.initCause(e);
            throw ioe;
        }
    }
}
