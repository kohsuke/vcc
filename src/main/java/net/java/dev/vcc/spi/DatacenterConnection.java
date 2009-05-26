package net.java.dev.vcc.spi;

import java.io.IOException;

public interface DatacenterConnection {
    boolean acceptsUrl(String url);

    AbstractDatacenter connect(String url, String username, char[] password) throws IOException;
}
