package net.java.dev.vcc.spi;

import net.java.dev.vcc.api.Log;
import net.java.dev.vcc.api.LogFactory;

import java.io.IOException;

public interface DatacenterConnection {
    boolean acceptsUrl(String url);

    AbstractDatacenter connect(String url, String username, char[] password, LogFactory logFactory) throws IOException;
}
