package net.java.dev.vcc.util;

/**
 * Used by tasks to determine if they should continue execution.
 */
public interface TaskController {
    /**
     * Returns {@code true} while the task can continue running.
     *
     * @return {@code true} while the task can continue running.
     */
    boolean isActive();
}
