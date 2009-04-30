package net.java.dev.vcc.spi;

import net.java.dev.vcc.api.Datacenter;

public interface DatacenterConnection {
    boolean acceptsUrl(String url);

    Datacenter connect(String url, String username, char[] password);
}
