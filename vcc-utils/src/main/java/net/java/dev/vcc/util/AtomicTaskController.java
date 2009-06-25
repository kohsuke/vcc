package net.java.dev.vcc.util;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A basic controller.
 */
public class AtomicTaskController implements TaskController {

    private final AtomicBoolean controller = new AtomicBoolean(true);

    public boolean isActive() {
        return controller.get();
    }

    public void stop() {
        controller.set(false);
    }
}
