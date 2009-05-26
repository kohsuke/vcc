package net.java.dev.vcc.spi;

import net.java.dev.vcc.api.Computer;
import net.java.dev.vcc.api.ManagedObjectId;

/**
 * Created by IntelliJ IDEA.
 * User: user
 * Date: 30-Apr-2009
 * Time: 17:40:05
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractComputer extends AbstractManagedObject<Computer> implements Computer {

    protected AbstractComputer(ManagedObjectId<Computer> id) {
        super(id);
    }
}
