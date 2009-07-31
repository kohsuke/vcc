package net.java.dev.vcc.impl.vmware.esx;

import net.java.dev.vcc.api.Datacenter;
import net.java.dev.vcc.api.ManagedObjectId;

/**
 * Created by IntelliJ IDEA. User: connollys Date: May 26, 2009 Time: 2:29:13 PM To change this template use File |
 * Settings | File Templates.
 */
class ViDatacenterId extends ManagedObjectId<Datacenter> {
    /**
     * Constructs a new {@link net.java.dev.vcc.api.ManagedObjectId}.
     *
     * @param datacenterUrl The URL of the {@link net.java.dev.vcc.api.Datacenter} hosting the instance referenced by
     *                      this ID.
     */
    ViDatacenterId(String datacenterUrl) {
        super(Datacenter.class, datacenterUrl);
    }
}
