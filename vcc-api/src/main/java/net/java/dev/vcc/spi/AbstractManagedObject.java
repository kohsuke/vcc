package net.java.dev.vcc.spi;

import net.java.dev.vcc.api.ManagedObject;
import net.java.dev.vcc.api.ManagedObjectId;

/**
 * Created by IntelliJ IDEA.
 * User: connollys
 * Date: May 26, 2009
 * Time: 2:16:00 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractManagedObject<T extends ManagedObject<T>> implements ManagedObject<T> {
    private final ManagedObjectId<T> id;

    protected AbstractManagedObject(ManagedObjectId<T> id) {
        this.id = id;
    }

    public ManagedObjectId<T> getId() {
        return id;
    }
}
