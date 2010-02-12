package net.java.dev.vcc.impl.vbox;

import net.java.dev.vcc.api.LogFactory;
import net.java.dev.vcc.spi.AbstractDatacenter;
import net.java.dev.vcc.spi.DatacenterConnection;

import java.io.IOException;

/**
 * A {@link net.java.dev.vcc.spi.DatacenterConnection} for VirtualBox.
 */
public class VBoxDatacenterConnection implements DatacenterConnection {

    public boolean acceptsUrl(String url) {
        return url.startsWith("vcc+vbox+http://") || url.startsWith("vcc+vbox+https://");
    }

    public AbstractDatacenter connect(String url, String username, char[] password, LogFactory logFactory)
            throws IOException {
    	return null;
    }

}
