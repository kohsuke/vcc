package net.java.dev.vcc.test;

import net.java.dev.vcc.Connection;
import net.java.dev.vcc.ConnectionProvider;

public class CrappyConnectionProvider implements ConnectionProvider {
    public boolean acceptsUrl(String url) {
        return url.startsWith("vcc:crappy:");
    }

    public Connection connect(String url, String username, char[] password) {
        return new CrappyConnection();
    }
}
