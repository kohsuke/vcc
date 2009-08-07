package net.java.dev.vcc.impl.vmware.esx;

import com.vmware.vim25.Event;

/**
 * A receiver of events.
 */
interface ViEventReceiver {
    /**
     * Called when there is an event for this event receiver.
     *
     * @param event the event.
     */
    void receiveEvent(Event event);
}
