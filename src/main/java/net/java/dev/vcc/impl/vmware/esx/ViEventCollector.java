package net.java.dev.vcc.impl.vmware.esx;

import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.Event;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.InvalidStateFaultMsg;
import com.vmware.vim25.EventFilterSpec;

import java.util.Queue;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.java.dev.vcc.api.LogFactory;
import net.java.dev.vcc.api.Log;

/**
 * Collects events and queues them up for later processing.
 */
final class ViEventCollector implements Runnable {

    private final ManagedObjectReference eventHistoryCollector;

    private final Queue<Event> events = new ConcurrentLinkedQueue<Event>();
    private ViDatacenter viDatacenter;
    private final Log log;

    public ViEventCollector(ViDatacenter viDatacenter, LogFactory logFactory)
            throws RuntimeFaultFaultMsg, InvalidStateFaultMsg {
        this.viDatacenter = viDatacenter;
        this.log = logFactory.getLog(getClass());

        this.eventHistoryCollector =
                viDatacenter.getConnection().getProxy()
                        .createCollectorForEvents(viDatacenter.getConnection().getServiceContent().getEventManager(),
                                new EventFilterSpec());
        viDatacenter.getConnection().getProxy().resetCollector(eventHistoryCollector);
    }

    public void run() {
        log.debug("Starting collecting events");
        try {
            boolean finished = false;
            while (!viDatacenter.isClosing() && !finished) {
                List<Event> events;
                try {
                    events = viDatacenter.getConnection().getProxy().readNextEvents(eventHistoryCollector, 100);
                }
                catch (RuntimeFaultFaultMsg e) {
                    viDatacenter.log(e);
                    return;
                }
                if (events.isEmpty()) {
                    finished = true;
                } else {
                    this.events.addAll(events);
                }
            }
            if (viDatacenter.isClosing() && finished) {
                this.events.add(new ViClosingConnectionEvent());
            }
        } finally {
            log.debug("Finished collecting events. Currently there are {0} events in the queue.", events.size());
        }
    }

    public Event poll() {
        return events.poll();
    }
}
