package net.java.dev.vcc;

import org.junit.Test;
import static org.junit.Assert.*;
import org.hamcrest.core.Is;
import org.hamcrest.core.AllOf;
import org.hamcrest.core.IsNull;
import net.java.dev.vcc.test.CrappyConnection;

public class SmokeTest {

    @Test
    public void smokes() {
        final Connection connection = VirtualComputerManagers.getConnection("vcc:crappy:localhost", "", "".toCharArray());
        assertThat(connection, IsNull.notNullValue());
        assertThat(connection, Is.is(CrappyConnection.class));
    }
}
