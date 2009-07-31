package net.java.dev.vcc.impl.vmware.esx;

import com.vmware.vim25.ManagedObjectReference;
import net.java.dev.vcc.api.Computer;

/**
 * The ID of a {@link net.java.dev.vcc.impl.vmware.esx.ViComputer}.
 */
class ViComputerId extends ViManagedObjectId<Computer> {

    /**
     * Constructs a new {@link net.java.dev.vcc.api.ManagedObjectId}.
     *
     * @param datacenterId The ID of the {@link net.java.dev.vcc.api.Datacenter} hosting the instance referenced by this
     *                     ID.
     * @param mo           The VMware ESX Managed Object Reference for this {@link Computer}.
     */
    ViComputerId(ViDatacenterId datacenterId, ManagedObjectReference mo) {
        super(Computer.class, datacenterId, mo);
    }

}
