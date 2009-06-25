package net.java.dev.vcc.util;

import static net.java.dev.vcc.util.RangeMatchers.greaterThan;
import static net.java.dev.vcc.util.RangeMatchers.lessThan;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultPollingTaskTest {
    private static final int INTERVAL = 10;

    private class MyTask implements Runnable {
        volatile boolean active = false;
        volatile boolean concurrent = false;
        final AtomicLong count = new AtomicLong(0);

        public void run() {
            concurrent |= active;
            active = true;
            try {
                count.incrementAndGet();
            } finally {
                active = false;
            }
        }
    }

    private final AtomicTaskController controller = new AtomicTaskController();

    private final MyTask task = new MyTask();

    private final DefaultPollingTask instance = new DefaultPollingTask(controller, task, INTERVAL, TimeUnit.MILLISECONDS);

    private final ExecutorService service = Executors.newCachedThreadPool();

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        controller.stop();
        service.shutdownNow();
    }

    @Test
    public void smokes() throws Exception {
        assertThat(task.count.get(), is(0L));
        long start = System.nanoTime();
        service.submit(instance);
        TimeUnit.MILLISECONDS.sleep(100);
        long actual = task.count.get();
        long duration = System.nanoTime() - start;
        assertThat("The clock has not rolled over during this test", duration, is(greaterThan(0L)));
        long estimate = duration / TimeUnit.MILLISECONDS.toNanos(INTERVAL);
        assertThat("We've slept for long enough", estimate, is(greaterThan(8)));
        assertThat(actual, is(allOf(greaterThan(estimate / 2), lessThan(estimate * 2))));
    }

}
