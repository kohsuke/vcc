package net.java.dev.vcc.spi;

import net.java.dev.vcc.api.ComputerSnapshot;
import net.java.dev.vcc.api.ManagedObjectId;

/**
 * Created by IntelliJ IDEA.
 * User: user
 * Date: 06-May-2009
 * Time: 17:54:46
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractComputerSnapshot extends AbstractManagedObject<ComputerSnapshot> implements ComputerSnapshot {
    protected AbstractComputerSnapshot(ManagedObjectId<ComputerSnapshot> id) {
        super(id);
    }
}
