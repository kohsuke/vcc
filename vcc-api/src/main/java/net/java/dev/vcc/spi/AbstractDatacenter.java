package net.java.dev.vcc.spi;

import net.java.dev.vcc.api.*;

import java.util.*;

/**
 * The base class from which all Service Provider Implementations map a connection from.
 */
public abstract class AbstractDatacenter extends AbstractManagedObject<Datacenter> implements Datacenter {

    private final Map<Class<? extends ManagedObject>, Set<Class<? extends Command>>> capabilities;

    public AbstractDatacenter(ManagedObjectId<Datacenter> id, Map.Entry<Class<? extends ManagedObject>, Set<Class<? extends Command>>>... capabilities) {
        super(id);
        Map<Class<? extends ManagedObject>, Set<Class<? extends Command>>> tmp
                = new HashMap<Class<? extends ManagedObject>, Set<Class<? extends Command>>>(capabilities.length);
        for (Map.Entry<Class<? extends ManagedObject>, Set<Class<? extends Command>>> capability : capabilities) {
            tmp.put(capability.getKey(), capability.getValue());
        }
        this.capabilities = Collections.unmodifiableMap(tmp);
    }

    public AbstractDatacenter(ManagedObjectId<Datacenter> id, CapabilityProfile base, Map.Entry<Class<? extends ManagedObject>, Set<Class<? extends Command>>>... capabilities) {
        super(id);
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

    public AbstractDatacenter(ManagedObjectId<Datacenter> id, boolean ignore, CapabilityProfile... bases) {
        super(id);
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

}
