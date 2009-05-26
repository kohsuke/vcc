package net.java.dev.vcc.test;

import net.java.dev.vcc.api.Datacenter;
import net.java.dev.vcc.api.ManagedObjectId;

/**
 * The id of a crappy datacenter.
 */
public class CrappyDatacenterId extends ManagedObjectId<Datacenter> {
    /**
     * {@inheritDoc}
     */
    protected CrappyDatacenterId(String datacenterUrl) {
        super(Datacenter.class, datacenterUrl);
    }
}
