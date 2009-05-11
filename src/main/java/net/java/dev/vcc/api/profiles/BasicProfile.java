package net.java.dev.vcc.api.profiles;

import net.java.dev.vcc.api.ManagedObject;
import net.java.dev.vcc.api.Command;
import net.java.dev.vcc.api.Datacenter;
import net.java.dev.vcc.api.Computer;
import net.java.dev.vcc.api.commands.StopComputer;
import net.java.dev.vcc.api.commands.StartComputer;
import net.java.dev.vcc.api.commands.RestartComputer;

import java.util.Map;
import java.util.Set;

/**
 * The most basic profile.
 */
public final class BasicProfile extends AbstractProfile {
    
    private static class ResourceHolder {
        private static final BasicProfile INSTANCE = new BasicProfile();
    }
    
    private BasicProfile(){
        super(with(Datacenter.class), 
                with(Computer.class, StartComputer.class, StopComputer.class, RestartComputer.class));
    }
    
    public static BasicProfile getInstance() {
        return ResourceHolder.INSTANCE;
    }
}
