package net.java.dev.vcc.test;

import net.java.dev.vcc.Connection;
import net.java.dev.vcc.VirtualComputerManager;

public class CrappyVCCManager implements VirtualComputerManager {
    public boolean acceptsUrl(String url) {
        return url.startsWith("vcc:crappy:");
    }

    public Connection connect(String url, String username, char[] password) {
        return new CrappyConnection();
    }
}
