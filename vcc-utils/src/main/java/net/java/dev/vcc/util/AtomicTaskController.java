package net.java.dev.vcc.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A basic controller.
 */
public class AtomicTaskController implements TaskController {

    private final Lock activeLock = new ReentrantLock();

    private final Condition deactivated = activeLock.newCondition();

    private boolean active = true;

    public boolean isActive() {
        activeLock.lock();
        try {
            return active;
        } finally {
            activeLock.unlock();
        }
    }

    public void awaitDeactivated() throws InterruptedException {
        activeLock.lock();
        try {
            while (active) {
                deactivated.await();
            }
        } finally {
            activeLock.unlock();
        }
    }

    public boolean awaitDeactivated(long timeout, TimeUnit unit) throws InterruptedException {
        activeLock.lock();
        try {
            long nanosTimeout = unit.toNanos(timeout);
            while (active) {
                if (nanosTimeout > 0) {
                    nanosTimeout = deactivated.awaitNanos(nanosTimeout);
                } else {
                    return false;
                }
            }
            return true;
        } finally {
            activeLock.unlock();
        }
    }

    public void stop() {
        activeLock.lock();
        try {
            active = false;
            deactivated.signalAll();
        } finally {
            activeLock.unlock();
        }
    }
}
