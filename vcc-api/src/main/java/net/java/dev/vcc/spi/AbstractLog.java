package net.java.dev.vcc.spi;

import net.java.dev.vcc.api.Log;

/**
 * Base class for {@link Log} implementations
 */
public abstract class AbstractLog implements Log {
    private static final String FQCN = AbstractLog.class.getName();

    protected abstract void debug(String fqcn, Throwable throwable, String messageKey, Object[] args);

    protected abstract void info(String fqcn, Throwable throwable, String messageKey, Object[] args);

    protected abstract void warn(String fqcn, Throwable throwable, String messageKey, Object[] args);

    protected abstract void error(String fqcn, Throwable throwable, String messageKey, Object[] args);

    protected abstract void fatal(String fqcn, Throwable throwable, String messageKey, Object[] args);

    public void debug(String messageKey) {
        if (isDebugEnabled()) {
            debug(FQCN, null, messageKey, null);
        }
    }

    public void debug(String messageKey, Object... args) {
        if (isDebugEnabled()) {
            debug(FQCN, null, messageKey, args);
        }
    }

    public void debug(Throwable t, String messageKey) {
        if (isDebugEnabled()) {
            debug(FQCN, t, messageKey, null);
        }
    }

    public void debug(Throwable t, String messageKey, Object... args) {
        if (isDebugEnabled()) {
            debug(FQCN, t, messageKey, args);
        }
    }

    public void info(String messageKey) {
        if (isInfoEnabled()) {
            info(FQCN, null, messageKey, null);
        }
    }

    public void info(String messageKey, Object... args) {
        if (isInfoEnabled()) {
            info(FQCN, null, messageKey, args);
        }
    }

    public void info(Throwable t, String messageKey) {
        if (isInfoEnabled()) {
            info(FQCN, t, messageKey, null);
        }
    }

    public void info(Throwable t, String messageKey, Object... args) {
        if (isInfoEnabled()) {
            info(FQCN, t, messageKey, args);
        }
    }

    public void warn(String messageKey) {
        if (isWarnEnabled()) {
            warn(FQCN, null, messageKey, null);
        }
    }

    public void warn(String messageKey, Object... args) {
        if (isWarnEnabled()) {
            warn(FQCN, null, messageKey, args);
        }
    }

    public void warn(Throwable t, String messageKey) {
        if (isWarnEnabled()) {
            warn(FQCN, t, messageKey, null);
        }
    }

    public void warn(Throwable t, String messageKey, Object... args) {
        if (isWarnEnabled()) {
            warn(FQCN, t, messageKey, args);
        }
    }

    public void error(String messageKey) {
        if (isErrorEnabled()) {
            error(FQCN, null, messageKey, null);
        }
    }

    public void error(String messageKey, Object... args) {
        if (isErrorEnabled()) {
            error(FQCN, null, messageKey, args);
        }
    }

    public void error(Throwable t, String messageKey) {
        if (isErrorEnabled()) {
            error(FQCN, t, messageKey, null);
        }
    }

    public void error(Throwable t, String messageKey, Object... args) {
        if (isErrorEnabled()) {
            error(FQCN, t, messageKey, args);
        }
    }

    public void fatal(String messageKey) {
        if (isFatalEnabled()) {
            fatal(FQCN, null, messageKey, null);
        }
    }

    public void fatal(String messageKey, Object... args) {
        if (isFatalEnabled()) {
            fatal(FQCN, null, messageKey, args);
        }
    }

    public void fatal(Throwable t, String messageKey) {
        if (isFatalEnabled()) {
            fatal(FQCN, t, messageKey, null);
        }
    }

    public void fatal(Throwable t, String messageKey, Object... args) {
        if (isFatalEnabled()) {
            fatal(FQCN, t, messageKey, args);
        }
    }
}
