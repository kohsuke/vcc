package net.java.dev.vcc.impl.vmware.esx;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Exposes the vmware test environment details
 */
public final class Environment {

    private Environment() {
        throw new IllegalAccessError("Utility class");
    }

    private static final class ResourceHolder {
        private static final Properties env = loadProperties();
    }

    private static Properties loadProperties() {
        Properties result = new Properties();
        InputStream stream = null;
        try {
            Environment.class.getResourceAsStream("/test-vmware-esx.properties");
            result.load(stream);
        } catch (Throwable e) {
            // ignore
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        stream = null;
        try {
            stream = new FileInputStream(
                    System.getProperty("test.vmware.esx.properties", "./test-vmware-esx.properties"));
            result.load(stream);
        } catch (Throwable e) {
            // ignore
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return result;
    }

    public static String getUrl() {
        return System.getProperty("test.vmware.esx.url", ResourceHolder.env.getProperty("url", ""));
    }

    public static String getUsername() {
        return System.getProperty("test.vmware.esx.username", ResourceHolder.env.getProperty("username", ""));
    }

    public static String getPassword() {
        return System.getProperty("test.vmware.esx.password", ResourceHolder.env.getProperty("password", ""));
    }
}
