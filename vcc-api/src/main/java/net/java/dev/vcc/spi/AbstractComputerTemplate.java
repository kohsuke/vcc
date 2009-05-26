package net.java.dev.vcc.spi;

import net.java.dev.vcc.api.ComputerTemplate;
import net.java.dev.vcc.api.ManagedObjectId;

/**
 * Created by IntelliJ IDEA.
 * User: user
 * Date: 06-May-2009
 * Time: 17:56:13
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractComputerTemplate extends AbstractManagedObject<ComputerTemplate> implements ComputerTemplate {
    protected AbstractComputerTemplate(ManagedObjectId<ComputerTemplate> id) {
        super(id);
    }
}
