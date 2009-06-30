package net.java.dev.vcc.impl.vmware.esx;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;

/**
 * Created by IntelliJ IDEA. User: connollys Date: Jun 30, 2009 Time: 4:36:49 PM To change this template use File |
 * Settings | File Templates.
 */
public final class JavaBeanHelper {

    private JavaBeanHelper() throws IllegalAccessException {
        throw new IllegalAccessException("Utility class");
    }

    public static void describe(Object bean)
            throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        System.out.println(toString(bean));
    }

    public static String toString(Object bean)
            throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        if (bean == null) {
            return "null";
        }
        StringBuilder buf = new StringBuilder(bean.getClass().getSimpleName());
        buf.append('@');
        buf.append(bean.hashCode());
        buf.append("[\n");
        for (PropertyDescriptor pd : Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors()) {
            if (pd.getReadMethod() != null) {
                buf.append("    ");
                buf.append(pd.getName());
                buf.append(" = ");
                Object o = pd.getReadMethod().invoke(bean);
                if (o == null) {
                    buf.append("null");
                } else if (o.getClass().isArray()) {
                    Object[] a = (Object[]) o;
                    buf.append("[");
                    boolean first = true;
                    for (Object i : a) {
                        if (first) {
                            first = false;
                        } else {
                            buf.append(",\n        ");
                        }
                        buf.append(toString(i).replace("\n", "\n            "));
                    }
                    buf.append("]");
                } else if (o instanceof Collection) {
                    Collection a = (Collection) o;
                    buf.append("[");
                    boolean first = true;
                    for (Object i : a) {
                        if (first) {
                            first = false;
                        } else {
                            buf.append(",\n        ");
                        }
                        buf.append(toString(i).replace("\n", "\n            "));
                    }
                    buf.append("]");
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
                    buf.append(toString(o).replace("\n", "\n        "));
                }
                buf.append("\n");
            }
        }
        buf.append("]");
        return buf.toString();
    }
}
