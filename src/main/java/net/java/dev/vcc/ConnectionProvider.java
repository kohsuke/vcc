package net.java.dev.vcc;

public interface ConnectionProvider {
    boolean acceptsUrl(String url);

    Connection connect(String url, String username, char[] password);
}
