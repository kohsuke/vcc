package net.java.dev.vcc.impl.vmware.esx;

import net.java.dev.vcc.api.Computer;
import net.java.dev.vcc.api.ComputerTemplate;
import net.java.dev.vcc.api.Datacenter;
import net.java.dev.vcc.api.DatacenterResourceGroup;
import net.java.dev.vcc.api.Host;
import net.java.dev.vcc.api.HostResourceGroup;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assume.*;
import org.junit.*;

/**
 * Created by IntelliJ IDEA. User: connollys Date: Jul 30, 2009 Time: 12:57:24 PM To change this template use File |
 * Settings | File Templates.
 */
public class SmokeTest {
    private static final String URL = Environment.getUrl();

    private static final String USERNAME = Environment.getUsername();

    private static final String PASSWORD = Environment.getPassword();

    @Test
    public void smokeTest()
            throws Exception {

        assumeThat(URL, notNullValue()); // need a test environment to run this test
        assumeThat(URL, is(not(""))); // need a test environment to run this test

        Datacenter datacenter =
                new ViDatacenterConnection().connect("vcc+vi+" + URL, USERNAME, PASSWORD.toCharArray());
        showDatacenter(datacenter);
//        JavaBeanHelper.describe(datacenter);
//        try {
//            Thread.sleep(30000);
//        } finally {
//            if (datacenter != null) {
//                datacenter.close();
//            }
//        }
    }

    private void showDatacenter(Datacenter datacenter) {
        System.out.println("Datacenter");
        for (DatacenterResourceGroup rg : datacenter.getDatacenterResourceGroups()) {
            showDatacenterResourceGroup(rg, " |--");
        }
        for (Host h : datacenter.getHosts()) {
            showHost(h, " |--");
        }
        for (ComputerTemplate c : datacenter.getComputerTemplates()) {
            showComputerTemplate(c, " |--");
        }
    }

    private void showDatacenterResourceGroup(DatacenterResourceGroup rg, String s) {
        System.out.println(s + "Group: " + rg.getName());
        for (DatacenterResourceGroup r : rg.getDatacenterResourceGroups()) {
            showDatacenterResourceGroup(r, s.replace('-', ' ') + " |--");
        }
        for (Host h : rg.getHosts()) {
            showHost(h, s.replace('-', ' ') + " |--");
        }
        for (ComputerTemplate c : rg.getComputerTemplates()) {
            showComputerTemplate(c, s.replace('-', ' ') + " |--");
        }
    }

    private void showHostResourceGroup(HostResourceGroup rg, String s) {
        System.out.println(s + "ResourcePool: " + rg.getName());
        for (HostResourceGroup r : rg.getHostResourceGroups()) {
            showHostResourceGroup(r, s.replace('-', ' ') + " |--");
        }
        for (Computer c : rg.getComputers()) {
            showComputer(c, s.replace('-', ' ') + " |--");
        }
        for (ComputerTemplate c : rg.getComputerTemplates()) {
            showComputerTemplate(c, s.replace('-', ' ') + " |--");
        }
    }

    private void showHost(Host h, String s) {
        System.out.println(s + "Host: " + h.getName());
        for (HostResourceGroup r : h.getHostResourceGroups()) {
            showHostResourceGroup(r, s.replace('-', ' ') + " |--");
        }
        for (Computer c : h.getComputers()) {
            showComputer(c, s.replace('-', ' ') + " |--");
        }
        for (ComputerTemplate c : h.getComputerTemplates()) {
            showComputerTemplate(c, s.replace('-', ' ') + " |--");
        }
    }

    private void showComputer(Computer c, String s) {
        System.out.println(s + "Computer: " + c.getName());
    }

    private void showComputerTemplate(ComputerTemplate c, String s) {
        System.out.println(s + "Template: " + c.getName());
    }
}
