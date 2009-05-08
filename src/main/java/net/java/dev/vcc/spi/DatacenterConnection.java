package net.java.dev.vcc.spi;

public interface DatacenterConnection {
    boolean acceptsUrl(String url);

    AbstractDatacenter connect(String url, String username, char[] password);
}
