package net.java.dev.vcc.test;

import net.java.dev.vcc.api.Datacenter;
import net.java.dev.vcc.spi.DatacenterConnection;

public class CrappyDatacenterConnection
    implements DatacenterConnection
{
    public boolean acceptsUrl( String url )
    {
        return url.startsWith( "vcc:crappy:" );
    }

    public Datacenter connect( String url, String username, char[] password )
    {
        return new CrappyDatacenter();
    }
}
