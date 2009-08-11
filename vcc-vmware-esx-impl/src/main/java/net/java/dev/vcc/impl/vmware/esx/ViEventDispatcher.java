package net.java.dev.vcc.impl.vmware.esx;

import com.vmware.vim25.Event;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ResourcePoolEvent;
import com.vmware.vim25.TaskEvent;
import com.vmware.vim25.VmEvent;
import net.java.dev.vcc.api.Log;
import net.java.dev.vcc.api.LogFactory;
import net.java.dev.vcc.spi.AbstractManagedObject;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Created by IntelliJ IDEA. User: connollys Date: Aug 10, 2009 Time: 1:25:21 PM To change this template use File |
 * Settings | File Templates.
 */
final class ViEventDispatcher implements Runnable {
    private ViDatacenter viDatacenter;
    private final Log log;

    public ViEventDispatcher(ViDatacenter viDatacenter, LogFactory logFactory) {
        this.viDatacenter = viDatacenter;
        log = logFactory.getLog(getClass());
    }

    public void run() {
        log.debug("Event dispatcher thread started.");
        try {
            while (!viDatacenter.isClosing()) {
                final Event event = viDatacenter.getEventCollector().poll();
                if (null == event) {
                    continue;
                }
                if (event instanceof ViClosingConnectionEvent) {
                    log.debug("Received connection closing event");
                    break;
                }
                log.debug("Received event: {0}", event.getClass());
                ManagedObjectReference ref = null;
                if (event instanceof VmEvent) {
                    ref = event.getVm().getVm();
                } else if (event instanceof ResourcePoolEvent) {
                    ref = ((ResourcePoolEvent) event).getResourcePool().getResourcePool();
                } else if (event instanceof TaskEvent) {
                    viDatacenter.processTask((TaskEvent) event);
                } else {
//                    try {
//                        log.info("Received event:\n{0}", toString(event));
//                    } catch (IntrospectionException e) {
//                        log.info(e, e.getMessage());
//                    } catch (IllegalAccessException e) {
//                        log.info(e, e.getMessage());
//                    } catch (InvocationTargetException e) {
//                        log.info(e, e.getMessage());
//                    }
                }
                if (ref != null) {
                    final AbstractManagedObject managedObject = viDatacenter.getManagedObject(ref);
                    if (managedObject instanceof ViEventReceiver) {
                        ((ViEventReceiver) managedObject).receiveEvent(event);
                    } else if (managedObject == null) {
                        // try creating it
                    }
                }

            }
        } finally {
            log.debug("Event dispatcher thread stopped.");
        }
    }

    public static String toString(Object bean)
            throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        if (bean == null) {
            return "null";
        }
        if (bean.getClass().getPackage().getName().startsWith("java")) {
            return bean.toString();
        }
        StringBuilder buf = new StringBuilder(bean.getClass().getSimpleName());
        try {
            buf.append('@');
            buf.append(bean.hashCode());
            buf.append("[\n");
            for (PropertyDescriptor pd : Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors()) {
                if (pd.getReadMethod() != null && !Modifier.isStatic(pd.getReadMethod().getModifiers()) && !"getClass"
                        .equals(pd.getReadMethod().getName())) {
                    buf.append("    ");
                    buf.append(pd.getName());
                    buf.append(" = ");
                    Object o = pd.getReadMethod().invoke(bean);
                    if (o == null) {
                        buf.append("null");
                    } else if (o.getClass().isArray()) {
                        if (o instanceof byte[]) {
                            buf.append(Arrays.toString((byte[]) o));
                        } else if (o instanceof boolean[]) {
                            buf.append(Arrays.toString((boolean[]) o));
                        } else if (o instanceof char[]) {
                            buf.append(Arrays.toString((char[]) o));
                        } else if (o instanceof double[]) {
                            buf.append(Arrays.toString((double[]) o));
                        } else if (o instanceof float[]) {
                            buf.append(Arrays.toString((float[]) o));
                        } else if (o instanceof int[]) {
                            buf.append(Arrays.toString((int[]) o));
                        } else if (o instanceof long[]) {
                            buf.append(Arrays.toString((long[]) o));
                        } else if (o instanceof short[]) {
                            buf.append(Arrays.toString((short[]) o));
                        } else {
                            Object[] a = (Object[]) o;
                            buf.append("[");
                            boolean first = true;
                            for (Object i : a) {
                                if (first) {
                                    first = false;
                                    buf.append("\n        ");
                                } else {
                                    buf.append(",\n        ");
                                }
                                buf.append(toString(i).replace("\n", "\n        "));
                            }
                            buf.append("]");
                            if (!first) {
                                buf.append('\n');
                            }
                        }
                    } else if (o instanceof Collection) {
                        Collection a = (Collection) o;
                        buf.append("[");
                        boolean first = true;
                        for (Object i : a) {
                            if (first) {
                                first = false;
                                buf.append("\n        ");
                            } else {
                                buf.append(",\n        ");
                            }
                            buf.append(toString(i).replace("\n", "\n        "));
                        }
                        buf.append("]");
                        if (!first) {
                            buf.append('\n');
                        }
                    } else if (o instanceof Map) {
                        Map<?, ?> a = (Map<?, ?>) o;
                        buf.append("[");
                        boolean first = true;
                        for (Map.Entry<?, ?> i : a.entrySet()) {
                            if (first) {
                                first = false;
                            } else {
                                buf.append(",\n        ");
                            }
                            buf.append(toString(i.getKey()).replace("\n", "\n            "));
                            buf.append("->");
                            buf.append(toString(i.getValue()).replace("\n", "\n            "));
                        }
                        buf.append("]");
                    } else if (o.getClass().getPackage().getName().startsWith("java")) {
                        buf.append(o);
                    } else {
                        buf.append(toString(o).replace("\n", "\n    "));
                    }
                    buf.append("\n");
                }
            }
        } catch (Throwable t) {
            buf.append("\n    *** Threw exception while processing ***\n");
            StringWriter sw = new StringWriter();
            try {
                PrintWriter pw = new PrintWriter(sw);
                t.printStackTrace(pw);
                pw.close();
                sw.close();
            } catch (IOException e) {
                throw new RuntimeException("This should never happen", e);
            }
            buf.append(sw.toString());
        }
        buf.append("]");
        return buf.toString();
    }
}
