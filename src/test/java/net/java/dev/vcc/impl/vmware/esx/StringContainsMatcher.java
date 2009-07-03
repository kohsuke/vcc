package net.java.dev.vcc.impl.vmware.esx;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * Created by IntelliJ IDEA.
* User: user
* Date: 03-Jul-2009
* Time: 11:01:32
* To change this template use File | Settings | File Templates.
*/
public class StringContainsMatcher extends BaseMatcher<String> {
    final String target;

    public StringContainsMatcher(String target) {
        this.target = target;
    }

    public boolean matches(Object o) {
        return o.toString().contains(target);
    }

    public void describeTo(Description description) {
        description.appendText("contains the string \"");
        description.appendText(target);
        description.appendText("\"");
    }
}
