package net.java.dev.vcc;

import org.junit.Test;
import static org.junit.Assert.*;
import org.hamcrest.core.Is;

public class SmokeTest {

    @Test
    public void smokes() {
        assertThat(true, Is.is(Boolean.TRUE));
    }
}
