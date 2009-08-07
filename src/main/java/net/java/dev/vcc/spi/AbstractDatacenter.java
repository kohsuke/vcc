package net.java.dev.vcc.spi;

import net.java.dev.vcc.api.CapabilityProfile;
import net.java.dev.vcc.api.Command;
import net.java.dev.vcc.api.Computer;
import net.java.dev.vcc.api.ComputerTemplate;
import net.java.dev.vcc.api.Datacenter;
import net.java.dev.vcc.api.DatacenterResourceGroup;
import net.java.dev.vcc.api.Host;
import net.java.dev.vcc.api.HostResourceGroup;
import net.java.dev.vcc.api.ManagedObject;
import net.java.dev.vcc.api.ManagedObjectId;
import net.java.dev.vcc.api.Log;
import net.java.dev.vcc.api.LogFactory;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The base class from which all Service Provider Implementations map a connection from.
 */
public abstract class AbstractDatacenter
        extends AbstractManagedObject<Datacenter>
        implements Datacenter {

    private final Log log;

    private final Map<Class<? extends ManagedObject>, Set<Class<? extends Command>>> capabilities;

    public AbstractDatacenter(LogFactory logFactory, ManagedObjectId<Datacenter> id,
                              Map.Entry<Class<? extends ManagedObject>, Set<Class<? extends Command>>>... capabilities) {
        super(id);
        log = logFactory.getLog(getClass());
        Map<Class<? extends ManagedObject>, Set<Class<? extends Command>>> tmp =
                new HashMap<Class<? extends ManagedObject>, Set<Class<? extends Command>>>(capabilities.length);
        for (Map.Entry<Class<? extends ManagedObject>, Set<Class<? extends Command>>> capability : capabilities) {
            tmp.put(capability.getKey(), capability.getValue());
        }
        this.capabilities = Collections.unmodifiableMap(tmp);
    }

    public AbstractDatacenter(LogFactory logFactory, ManagedObjectId<Datacenter> id, CapabilityProfile base,
                              Map.Entry<Class<? extends ManagedObject>, Set<Class<? extends Command>>>... capabilities) {
        super(id);
        log = logFactory.getLog(getClass());
        Map<Class<? extends ManagedObject>, Set<Class<? extends Command>>> tmp =
                new HashMap<Class<? extends ManagedObject>, Set<Class<? extends Command>>>(capabilities.length);
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

    public AbstractDatacenter(LogFactory logFactory, ManagedObjectId<Datacenter> id, boolean ignore, CapabilityProfile... bases) {
        super(id);
        log = logFactory.getLog(getClass());
        Map<Class<? extends ManagedObject>, Set<Class<? extends Command>>> tmp =
                new HashMap<Class<? extends ManagedObject>, Set<Class<? extends Command>>>();
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

    public final Log getLog() {
        return log;
    }

    /**
     * Helper method.
     *
     * @param object   The type of {@link net.java.dev.vcc.api.ManagedObject}
     * @param commands The types of {@link net.java.dev.vcc.api.Command}s supported on the {@link
     *                 net.java.dev.vcc.api.ManagedObject}.
     *
     * @return An {@link java.util.Map.Entry} for use in the constructor.
     */
    protected static Map.Entry<Class<? extends ManagedObject>, Set<Class<? extends Command>>> with(
            Class<? extends ManagedObject> object, Class<? extends Command>... commands) {
        return new AbstractMap.SimpleImmutableEntry<Class<? extends ManagedObject>, Set<Class<? extends Command>>>(
                object, Collections.unmodifiableSet(new HashSet<Class<? extends Command>>(Arrays.asList(commands))));
    }

    /**
     * {@inheritDoc}
     */
    public Set<ComputerTemplate> getComputerTemplates() {
        return Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     */
    public Set<Host> getHosts() {
        return Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     */
    public Set<DatacenterResourceGroup> getDatacenterResourceGroups() {
        return Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     */
    public Set<Host> getAllHosts() {
        Set<Host> result = new HashSet<Host>(getHosts());
        for (DatacenterResourceGroup group : getDatacenterResourceGroups()) {
            result.addAll(group.getAllHosts());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Set<ComputerTemplate> getAllComputerTemplates() {
        Set<ComputerTemplate> result = new HashSet<ComputerTemplate>(getComputerTemplates());
        for (DatacenterResourceGroup group : getDatacenterResourceGroups()) {
            result.addAll(group.getAllComputerTemplates());
        }
        for (Host host : getHosts()) {
            result.addAll(host.getAllComputerTemplates());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Set<Computer> getAllComputers() {
        Set<Computer> result = new HashSet<Computer>();
        for (DatacenterResourceGroup group : getDatacenterResourceGroups()) {
            result.addAll(group.getAllComputers());
        }
        for (Host host : getHosts()) {
            result.addAll(host.getAllComputers());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Set<DatacenterResourceGroup> getAllDatacenterResourceGroups() {
        Set<DatacenterResourceGroup> result = new HashSet<DatacenterResourceGroup>(getDatacenterResourceGroups());
        for (DatacenterResourceGroup group : getDatacenterResourceGroups()) {
            result.addAll(group.getAllDatacenterResourceGroups());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Set<HostResourceGroup> getAllHostResourceGroups() {
        Set<HostResourceGroup> result = new HashSet<HostResourceGroup>();
        for (DatacenterResourceGroup group : getDatacenterResourceGroups()) {
            result.addAll(group.getAllHostResourceGroups());
        }
        for (Host host : getHosts()) {
            result.addAll(host.getAllHostResourceGroups());
        }
        return result;
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
