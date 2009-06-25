package net.java.dev.vcc.util;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * Created by IntelliJ IDEA.
 * User: user
 * Date: 25-Jun-2009
 * Time: 08:51:33
 * To change this template use File | Settings | File Templates.
 */
public class RangeMatchers {
    public static GreaterThan greaterThan(long limit) {
        return new GreaterThan(limit);
    }

    public static AtLeast atLeast(long limit) {
        return new AtLeast(limit);
    }

    public static LessThan lessThan(long limit) {
        return new LessThan(limit);
    }

    public static AtMost atMost(long limit) {
        return new AtMost(limit);
    }

    private static class GreaterThan extends BaseMatcher<Long> {

        private final long limit;

        public GreaterThan(long limit) {
            this.limit = limit;
        }

        public boolean matches(Object o) {
            return Long.class.cast(o) > limit;
        }

        public void describeTo(Description description) {
            description.appendText("greater than ");
            description.appendValue(limit);
        }
    }

    private static class AtLeast extends BaseMatcher<Long> {

        private final long limit;

        public AtLeast(long limit) {
            this.limit = limit;
        }

        public boolean matches(Object o) {
            return Long.class.cast(o) >= limit;
        }

        public void describeTo(Description description) {
            description.appendText("at least ");
            description.appendValue(limit);
        }
    }

    private static class LessThan extends BaseMatcher<Long> {

        private final long limit;

        public LessThan(long limit) {
            this.limit = limit;
        }

        public boolean matches(Object o) {
            return Long.class.cast(o) < limit;
        }

        public void describeTo(Description description) {
            description.appendText("less than ");
            description.appendValue(limit);
        }
    }

    private static class AtMost extends BaseMatcher<Long> {

        private final long limit;

        public AtMost(long limit) {
            this.limit = limit;
        }

        public boolean matches(Object o) {
            return Long.class.cast(o) <= limit;
        }

        public void describeTo(Description description) {
            description.appendText("at most ");
            description.appendValue(limit);
        }
    }
}
