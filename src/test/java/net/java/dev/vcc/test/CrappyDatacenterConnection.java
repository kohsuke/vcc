package net.java.dev.vcc.test;

import net.java.dev.vcc.spi.AbstractDatacenter;
import net.java.dev.vcc.spi.DatacenterConnection;
import net.java.dev.vcc.api.LogFactory;

public class CrappyDatacenterConnection
        implements DatacenterConnection {
    public boolean acceptsUrl(String url) {
        return url.startsWith("vcc:crappy:");
    }

    public AbstractDatacenter connect(String url, String username, char[] password, LogFactory logFactory) {
        return new CrappyDatacenter(new CrappyDatacenterId(url), logFactory);
    }
}
