package net.java.dev.vcc.ant;

import net.java.dev.vcc.api.Datacenter;
import net.java.dev.vcc.api.Computer;
import net.java.dev.vcc.api.PowerState;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Comparator;

/**
 * Lists the computers in a datacenter.
 */
public class ListComputersTask extends AbstractDatacenterTask {
    protected void execute(Datacenter datacenter) throws BuildException {
        SortedSet<Computer> computers = new TreeSet<Computer>(new Comparator<Computer>() {
            public int compare(Computer o1, Computer o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        computers.addAll(datacenter.getAllComputers());
        for (Computer computer : computers) {
            PowerState state = computer.getState();
            log(rightPad("\"" + computer.getName() + "\" ", 70 - state.toString().length(), ".") + " "
                    + state.toString(), Project.MSG_INFO);
        }
    }

    private String rightPad(String s, int i, String s1) {
        StringBuffer buf = new StringBuffer(i);
        buf.append(s);
        while (buf.length() < i) {
            buf.append(s1.substring(0, Math.min(s1.length(), i - buf.length())));
        }
        return buf.toString();
    }
}
