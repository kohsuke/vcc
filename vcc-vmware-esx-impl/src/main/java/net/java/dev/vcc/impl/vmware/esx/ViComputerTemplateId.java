package net.java.dev.vcc.impl.vmware.esx;

import com.vmware.vim25.ManagedObjectReference;
import net.java.dev.vcc.api.ComputerTemplate;

/**
 * The ID of a {@link ViComputerTemplate}.
 */
class ViComputerTemplateId extends ViManagedObjectId<ComputerTemplate> {

    /**
     * Constructs a new {@link net.java.dev.vcc.api.ManagedObjectId}.
     *
     * @param datacenterId The ID of the {@link net.java.dev.vcc.api.Datacenter} hosting the instance referenced by this
     *                     ID.
     * @param mo           The VMware ESX Managed Object Reference for this {@link net.java.dev.vcc.api.Computer}.
     */
    ViComputerTemplateId(ViDatacenterId datacenterId, ManagedObjectReference mo) {
        super(ComputerTemplate.class, datacenterId, mo);
    }

}