package net.java.dev.vcc.api;

/**
 * Represeents a group of resources, such as: {@link net.java.dev.vcc.api.Host}s for virtual {@link
 * net.java.dev.vcc.api.Computer}s; virtual {@link net.java.dev.vcc.api.Computer}s; and {@link HostResourceGroup}s.
 *
 * @author Stephen Connolly
 */
public interface HostResourceGroup
        extends ManagedObject<HostResourceGroup>, HostResourceContainer {
}