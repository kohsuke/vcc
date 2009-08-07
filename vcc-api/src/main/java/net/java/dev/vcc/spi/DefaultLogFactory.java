package net.java.dev.vcc.spi;

import net.java.dev.vcc.api.LogFactory;
import net.java.dev.vcc.api.Log;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.text.MessageFormat;

/**
 * This is a {@link LogFactory} to use when there are no {@link LogFactory} instances available, it uses {@link
 * java.util.logging.Logger} for logging.
 */
class DefaultLogFactory extends AbstractLogFactory {


    protected Log newLog(String name, String bundleName) {
        return new JULLog(name, bundleName);
    }

    private static final class JULLog extends AbstractLog {

        private final Logger delegate;

        public JULLog(String name, String bundleName) {
            delegate = Logger.getLogger(name, bundleName);
        }

        public boolean isDebugEnabled() {
            return delegate.isLoggable(Level.FINE);
        }

        public boolean isInfoEnabled() {
            return delegate.isLoggable(Level.INFO);
        }

        public boolean isWarnEnabled() {
            return delegate.isLoggable(Level.WARNING);
        }

        public boolean isErrorEnabled() {
            return delegate.isLoggable(Level.SEVERE);
        }

        public boolean isFatalEnabled() {
            return delegate.isLoggable(Level.SEVERE);
        }

        protected void debug(String fqcn, Throwable throwable, String messageKey, Object[] args) {
            LogRecord rec = new LogRecord(Level.FINE, messageKey);
            rec.setParameters(args);
            rec.setThrown(throwable);
            inferCaller(fqcn, rec);
            delegate.log(rec);
        }

        protected void info(String fqcn, Throwable throwable, String messageKey, Object[] args) {
            LogRecord rec = new LogRecord(Level.INFO, messageKey);
            rec.setParameters(args);
            rec.setThrown(throwable);
            inferCaller(fqcn, rec);
            delegate.log(rec);
        }

        protected void warn(String fqcn, Throwable throwable, String messageKey, Object[] args) {
            LogRecord rec = new LogRecord(Level.WARNING, messageKey);
            rec.setParameters(args);
            rec.setThrown(throwable);
            inferCaller(fqcn, rec);
            delegate.log(rec);
        }

        protected void error(String fqcn, Throwable throwable, String messageKey, Object[] args) {
            LogRecord rec = new LogRecord(Level.SEVERE, messageKey);
            rec.setParameters(args);
            rec.setThrown(throwable);
            inferCaller(fqcn, rec);
            delegate.log(rec);
        }

        protected void fatal(String fqcn, Throwable throwable, String messageKey, Object[] args) {
            LogRecord rec = new LogRecord(Level.SEVERE, messageKey);
            rec.setParameters(args);
            rec.setThrown(throwable);
            inferCaller(fqcn, rec);
            delegate.log(rec);
        }

        // Private method to infer the caller's class and method names
        private void inferCaller(String fqcn, LogRecord rec) {
            // Get the stack trace.
            StackTraceElement stack[] = (new Throwable()).getStackTrace();
            // First, search back to a method in the Logger class.
            int ix = 0;
            while (ix < stack.length) {
                StackTraceElement frame = stack[ix];
                String cname = frame.getClassName();
                if (cname.equals(fqcn)) {
                    break;
                }
                ix++;
            }
            // Now search for the first frame before the "Logger" class.
            while (ix < stack.length) {
                StackTraceElement frame = stack[ix];
                String cname = frame.getClassName();
                if (!cname.equals(fqcn)) {
                    // We've found the relevant frame.
                    rec.setSourceClassName(cname);
                    rec.setSourceMethodName(frame.getMethodName());
                    return;
                }
                ix++;
            }
            // We haven't found a suitable frame, so just punt.  This is
            // OK as we are only committed to making a "best effort" here.
        }
    }
}
