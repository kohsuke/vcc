package net.java.dev.vcc.spi;

import net.java.dev.vcc.api.Host;
import net.java.dev.vcc.api.ManagedObjectId;

/**
 * Created by IntelliJ IDEA.
 * User: user
 * Date: 30-Apr-2009
 * Time: 17:40:33
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractHost extends AbstractManagedObject<Host> implements Host {
    protected AbstractHost(ManagedObjectId<Host> id) {
        super(id);
    }
}
