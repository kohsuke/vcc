package net.java.dev.vcc.api;

import java.util.Set;

/**
 * Represeents a group of resources, such as: {@link Host}s for virtual {@link Computer}s; virtual {@link Computer}s; and {@link ResourceGroup}s.
 *
 * @author Stephen Connolly
 */
public interface ResourceGroup extends ManagedObject<ResourceGroup>, ResourceContainer
{

}
