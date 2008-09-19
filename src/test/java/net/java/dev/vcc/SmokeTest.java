package net.java.dev.vcc;

import net.java.dev.vcc.test.CrappyConnection;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class SmokeTest {

    @Test
    public void smokes() {
        final Connection connection = ConnectionFactory.getConnection("vcc:crappy:localhost", "", "".toCharArray());
        assertThat(connection, IsNull.notNullValue());
        assertThat(connection, Is.is(CrappyConnection.class));
    }
}
