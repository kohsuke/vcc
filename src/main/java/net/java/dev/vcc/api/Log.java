package net.java.dev.vcc.api;

/**
 * A generic logging interface for logging from within VCC implementations.
 */
public interface Log {
    /**
     * Returns {@code true} if debug level messages are being logged.
     * @return {@code true} if debug level messages are being logged.
     */
    boolean isDebugEnabled();

    void debug(String messageKey);

    void debug(String messageKey, Object... args);

    void debug(Throwable t, String messageKey);

    void debug(Throwable t, String messageKey, Object... args);

    /**
     * Returns {@code true} if info level messages are being logged.
     * @return {@code true} if info level messages are being logged.
     */
    boolean isInfoEnabled();

    void info(String messageKey);
    void info(String messageKey, Object... args);

    void info(Throwable t, String messageKey);
    void info(Throwable t, String messageKey, Object... args);

    /**
     * Returns {@code true} if warn level messages are being logged.
     * @return {@code true} if warn level messages are being logged.
     */
    boolean isWarnEnabled();

    void warn(String messageKey);
    void warn(String messageKey, Object... args);

    void warn(Throwable t, String messageKey);
    void warn(Throwable t, String messageKey, Object... args);

    /**
     * Returns {@code true} if error level messages are being logged.
     * @return {@code true} if error level messages are being logged.
     */
    boolean isErrorEnabled();

    void error(String messageKey);
    void error(String messageKey, Object... args);

    void error(Throwable t, String messageKey);
    void error(Throwable t, String messageKey, Object... args);

    /**
     * Returns {@code true} if fatal level messages are being logged.
     * @return {@code true} if fatal level messages are being logged.
     */
    boolean isFatalEnabled();

    void fatal(String messageKey);
    void fatal(String messageKey, Object... args);

    void fatal(Throwable t, String messageKey);
    void fatal(Throwable t, String messageKey, Object... args);
}
