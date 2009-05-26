package net.java.dev.vcc.test;

import net.java.dev.vcc.spi.AbstractDatacenter;
import net.java.dev.vcc.spi.DatacenterConnection;

public class CrappyDatacenterConnection
        implements DatacenterConnection {
    public boolean acceptsUrl(String url) {
        return url.startsWith("vcc:crappy:");
    }

    public AbstractDatacenter connect(String url, String username, char[] password) {
        return new CrappyDatacenter(new CrappyDatacenterId(url));
    }
}
