package net.java.dev.vcc.ant;

import net.java.dev.vcc.spi.AbstractLogFactory;
import net.java.dev.vcc.spi.AbstractLog;
import net.java.dev.vcc.api.Log;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;

import java.text.MessageFormat;

/**
 * Created by IntelliJ IDEA. User: connollys Date: Aug 11, 2009 Time: 3:26:11 PM To change this template use File |
 * Settings | File Templates.
 */
public class AntLogFactory extends AbstractLogFactory {

    private static Task task = null;

    protected Log newLog(String name, String bundleName) {
        final Task task;
        synchronized (AntLogFactory.class) {
            task = AntLogFactory.task;
        }        
        return new AbstractLog() {
            @Override
            protected void debug(String fqcn, Throwable throwable, String messageKey, Object[] args) {
                task.log(MessageFormat.format(messageKey, args), Project.MSG_DEBUG);
            }

            @Override
            protected void info(String fqcn, Throwable throwable, String messageKey, Object[] args) {
                task.log(MessageFormat.format(messageKey, args), Project.MSG_INFO);
            }

            @Override
            protected void warn(String fqcn, Throwable throwable, String messageKey, Object[] args) {
                task.log(MessageFormat.format(messageKey, args), Project.MSG_WARN);
            }

            @Override
            protected void error(String fqcn, Throwable throwable, String messageKey, Object[] args) {
                task.log(MessageFormat.format(messageKey, args), Project.MSG_ERR);
            }

            @Override
            protected void fatal(String fqcn, Throwable throwable, String messageKey, Object[] args) {
                task.log(MessageFormat.format(messageKey, args), Project.MSG_ERR);
            }

            public boolean isDebugEnabled() {
                return true;
            }

            public boolean isInfoEnabled() {
                return true;
            }

            public boolean isWarnEnabled() {
                return true;
            }

            public boolean isErrorEnabled() {
                return true;
            }

            public boolean isFatalEnabled() {
                return true;
            }
        };
    }

    public static synchronized void setTask(Task task) {
        AntLogFactory.task = task;
    }
}
