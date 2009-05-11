package net.java.dev.vcc.api.profiles;

import net.java.dev.vcc.api.CapabilityProfile;
import net.java.dev.vcc.api.Command;
import net.java.dev.vcc.api.ManagedObject;

import java.util.*;

/**
 * A base class for all the standard profiles
 */
class AbstractProfile implements CapabilityProfile {
    private final Map<Class<? extends ManagedObject>, Set<Class<? extends Command>>> capabilities;

    public AbstractProfile(Map.Entry<Class<? extends ManagedObject>, Set<Class<? extends Command>>>... capabilities) {
        Map<Class<? extends ManagedObject>, Set<Class<? extends Command>>> tmp
                = new HashMap<Class<? extends ManagedObject>, Set<Class<? extends Command>>>(capabilities.length);
        for (Map.Entry<Class<? extends ManagedObject>, Set<Class<? extends Command>>> capability : capabilities) {
            tmp.put(capability.getKey(), capability.getValue());
        }
        this.capabilities = Collections.unmodifiableMap(tmp);
    }

    public AbstractProfile(CapabilityProfile base, Map.Entry<Class<? extends ManagedObject>, Set<Class<? extends Command>>>... capabilities) {
        Map<Class<? extends ManagedObject>, Set<Class<? extends Command>>> tmp
                = new HashMap<Class<? extends ManagedObject>, Set<Class<? extends Command>>>(capabilities.length);
        for (Map.Entry<Class<? extends ManagedObject>, Set<Class<? extends Command>>> capability : capabilities) {
            tmp.put(capability.getKey(), capability.getValue());
        }
        for (Class<? extends ManagedObject> moc : base.getObjectClasses()) {
            Set<Class<? extends Command>> foo = new HashSet<Class<? extends Command>>(base.getCommands(moc));
            if (tmp.containsKey(moc)) {
                foo.addAll(tmp.get(moc));
            }
            tmp.put(moc, Collections.unmodifiableSet(foo));
        }
        this.capabilities = Collections.unmodifiableMap(tmp);
    }

    public AbstractProfile(CapabilityProfile... bases) {
        Map<Class<? extends ManagedObject>, Set<Class<? extends Command>>> tmp
                = new HashMap<Class<? extends ManagedObject>, Set<Class<? extends Command>>>();
        for (CapabilityProfile base : bases) {
            for (Class<? extends ManagedObject> moc : base.getObjectClasses()) {
                Set<Class<? extends Command>> foo = new HashSet<Class<? extends Command>>(base.getCommands(moc));
                if (tmp.containsKey(moc)) {
                    foo.addAll(tmp.get(moc));
                }
                tmp.put(moc, Collections.unmodifiableSet(foo));
            }
        }
        this.capabilities = Collections.unmodifiableMap(tmp);
    }

    /**
     * Helper method.
     *
     * @param object   The type of {@link net.java.dev.vcc.api.ManagedObject}
     * @param commands The types of {@link net.java.dev.vcc.api.Command}s supported on the
     *                 {@link net.java.dev.vcc.api.ManagedObject}.
     * @return An {@link java.util.Map.Entry} for use in the constructor.
     */
    protected static Map.Entry<Class<? extends ManagedObject>, Set<Class<? extends Command>>> with(
            Class<? extends ManagedObject> object, Class<? extends Command>... commands) {
        return new AbstractMap.SimpleImmutableEntry<Class<? extends ManagedObject>, Set<Class<? extends Command>>>(
                object, Collections.unmodifiableSet(
                        new HashSet<Class<? extends Command>>(
                                Arrays.asList(commands)
                        )
                )
        );
    }

    /**
     * {@inheritDoc}
     */
    public final Set<Class<? extends Command>> getCommands(Class<? extends ManagedObject> managedObjectClass) {
        Set<Class<? extends Command>> result = capabilities.get(managedObjectClass);
        return result == null ? Collections.<Class<? extends Command>>emptySet() : result;
    }

    /**
     * {@inheritDoc}
     */
    public final Set<Class<? extends ManagedObject>> getObjectClasses() {
        return capabilities.keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CapabilityProfile)) {
            return false;
        }

        CapabilityProfile that = (CapabilityProfile) o;

        if (!capabilities.keySet().equals(that.getObjectClasses())) return false;
        for (Class<? extends ManagedObject> b : capabilities.keySet()) {
            if (!capabilities.get(b).equals(that.getCommands(b))) return false;
        }

        return true;
    }

    /**
     * Returns {@code true} if this {@link net.java.dev.vcc.api.CapabilityProfile} is supported by the
     * supplied {@link net.java.dev.vcc.api.CapabilityProfile}.
     *
     * @param that The {@link net.java.dev.vcc.api.CapabilityProfile} which this may be a subset of.
     * @return {@code true} if this {@link net.java.dev.vcc.api.CapabilityProfile} is supported by the
     *         supplied {@link net.java.dev.vcc.api.CapabilityProfile}.
     */
    public final boolean supportedBy(CapabilityProfile that) {
        if (this == that) {
            return true;
        }
        if (!capabilities.keySet().containsAll(that.getObjectClasses())) {
            return false;
        }
        for (Class<? extends ManagedObject> b : capabilities.keySet()) {
            if (!capabilities.get(b).containsAll(that.getCommands(b))) {
                return false;
            }
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public final boolean supports(CapabilityProfile that) {
        if (this == that) {
            return true;
        }
        if (!that.getObjectClasses().containsAll(capabilities.keySet())) {
            return false;
        }
        for (Class<? extends ManagedObject> b : that.getObjectClasses()) {
            if (!that.getCommands(b).containsAll(capabilities.get(b))) {
                return false;
            }
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return capabilities.keySet().hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        StringBuilder buf = new StringBuilder(512);
        buf.append("Profile[");
        boolean first = true;
        for (Map.Entry<Class<? extends ManagedObject>, Set<Class<? extends Command>>> b : capabilities.entrySet()) {
            if (!first) {
                buf.append(", ");
            } else {
                first = false;
            }
            buf.append(b.getKey().getSimpleName());
            buf.append('{');
            boolean innerFirst = true;
            for (Class<? extends Command> c : b.getValue()) {
                if (!innerFirst) {
                    buf.append(", ");
                } else {
                    innerFirst = false;
                }
                buf.append(c.getSimpleName());
            }
            buf.append('}');
        }
        buf.append(']');
        return buf.toString();
    }
}
