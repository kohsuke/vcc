package net.java.dev.vcc.impl.vmware.esx;

import com.vmware.vim25.InvalidLocaleFaultMsg;
import com.vmware.vim25.InvalidLoginFaultMsg;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import net.java.dev.vcc.spi.AbstractDatacenter;
import net.java.dev.vcc.spi.DatacenterConnection;

import java.io.IOException;

/**
 * A {@link net.java.dev.vcc.spi.DatacenterConnection} for VMware ESX.
 */
public class ViDatacenterConnection implements DatacenterConnection {
    public boolean acceptsUrl(String url) {
        return url.startsWith("vcc+vi+http://") || url.startsWith("vcc+vi+https://");
    }

    public AbstractDatacenter connect(String url, String username, char[] password) throws IOException {
        assert url.startsWith("vcc+vi+");
        try {
            ViConnection connection = new ViConnection(url.substring("vcc+vi+".length()), username, password);
            return new ViDatacenter(new ViDatacenterId(url), connection);
        } catch (RuntimeFaultFaultMsg e) {
            IOException ioe = new IOException(e.getMessage());
            ioe.initCause(e);
            throw ioe;
        } catch (InvalidLocaleFaultMsg e) {
            IOException ioe = new IOException(e.getMessage());
            ioe.initCause(e);
            throw ioe;
        } catch (InvalidLoginFaultMsg e) {
            IOException ioe = new IOException(e.getMessage());
            ioe.initCause(e);
            throw ioe;
        }
    }
}
