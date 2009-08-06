package net.java.dev.vcc.api;

/**
 * Represeents a group of resources, such as: {@link Host}s for virtual {@link Computer}s; virtual {@link Computer}s; and {@link DatacenterResourceGroup}s.
 *
 * @author Stephen Connolly
 */
public interface DatacenterResourceGroup
        extends ManagedObject<DatacenterResourceGroup>, DatacenterResourceContainer {

}
