package net.java.dev.vcc.impl.vmware.esx;

import net.java.dev.vcc.api.Datacenter;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assume.*;
import org.junit.*;

/**
 * Created by IntelliJ IDEA. User: connollys Date: Jul 30, 2009 Time: 12:57:24 PM To change this template use File |
 * Settings | File Templates.
 */
public class SmokeTest {
    private static final String URL = Environment.getUrl();

    private static final String USERNAME = Environment.getUsername();

    private static final String PASSWORD = Environment.getPassword();

    @Test
    public void smokeTest() throws Exception {

        assumeThat(URL, notNullValue()); // need a test environment to run this test
        assumeThat(URL, is(not(""))); // need a test environment to run this test

        Datacenter datacenter = new ViDatacenterConnection().connect("vcc+vi+" + URL, USERNAME, PASSWORD.toCharArray());
        JavaBeanHelper.describe(datacenter);
        try {
            Thread.sleep(30000);
        } finally {
            if (datacenter != null) {
                datacenter.close();
            }
        }
    }
}
