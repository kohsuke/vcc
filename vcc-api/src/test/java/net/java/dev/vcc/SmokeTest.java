package net.java.dev.vcc;

import net.java.dev.vcc.api.Datacenter;
import net.java.dev.vcc.api.DatacenterManager;
import net.java.dev.vcc.test.CrappyDatacenter;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import java.io.IOException;

public class SmokeTest {

    @Test
    public void smokes() throws IOException {
        final Datacenter datacenter = DatacenterManager.getConnection("vcc:crappy:localhost", "", "".toCharArray());
        try {
        assertThat(datacenter, IsNull.notNullValue());
        assertThat(datacenter, Is.is(CrappyDatacenter.class));
        } finally {
            datacenter.close();
        }
    }
}
