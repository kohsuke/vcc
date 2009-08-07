package net.java.dev.vcc.impl.vmwarevix;

import net.java.dev.vcc.spi.AbstractDatacenter;
import net.java.dev.vcc.spi.DatacenterConnection;
import net.java.dev.vcc.api.LogFactory;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The ConnectionProvider for VMware virtual computers controlled through the VMware VIX API.
 */
public class VixDatacenterConnection
        implements DatacenterConnection {
    public boolean acceptsUrl(String url) {
        return URL_PATTERN.matcher(url).matches();
    }

    public AbstractDatacenter connect(String url, String username, char[] password, LogFactory logFactory) {
        return null;
    }

    private final static Pattern URL_PATTERN =
            Pattern.compile("vcc\\:vmware-vix\\:([^\\:\\;\\&]+)(:\\d+)?([\\;\\&].*)?");

    private final static Pattern PARAMS_PATTERN =
            Pattern.compile("[\\;\\&]([a-zA-Z][a-zA-Z0-9\\-\\_]*)=([^\\;\\&]+)");

    ConnectionDetails parseUrl(String url) {
        Matcher m = URL_PATTERN.matcher(url);
        if (!m.matches()) {
            return null;
        }
        String host = m.group(1);
        String port = m.group(2);
        if (port == null) {
            port = "902";
        }

        String paramStr = m.group(3);

        final HashMap<String, String> params = new HashMap<String, String>();

        if (paramStr != null) {
            m = PARAMS_PATTERN.matcher(paramStr);
            while (m.find()) {
                params.put(m.group(1).toLowerCase(), m.group(2));
            }
        }

        String libraryPath = params.remove("library");

        return new ConnectionDetails(host, Integer.parseInt(port), libraryPath, params);
    }
}
