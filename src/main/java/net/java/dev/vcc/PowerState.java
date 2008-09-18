package net.java.dev.vcc;

/**
 * Represents the power states of a virtual machine.
 * <pre>
 *                   [OFF]
 *                    /|\
 *                     |               [MIGRATING]
 *                    \|/
 *                 [BOOTING]
 *                    /|\
 *                     |       _____________________________
 *                    \|/   |/_                             \
 * [MIGRATING]<----> [RUNNING] -----> [PAUSING] -----> [PAUSED]
 *                   /|\   |                             /
 *                    |    |          /-----------------/
 *                    |   \|/       |/_
 *            [RESUMING]  [SUSPENDING]
 *                 /|\     |
 *                  |      |
 *                  |     \|/
 *                [SUSPENDED]
 *
 * </pre>
 */
public enum PowerState {
    /**
     * The virtual machine is off.
     */
    OFF,
    /**
     * The virtual machine is in the process of booting up from the {@link #OFF} state.
     */
    BOOTING,
    /**
     * The virtual machine is active and has finished its boot sequence.
     */
    RUNNING,
    /**
     * The virtual machine is in the process of transitioning to the {@link #PAUSED} state,
     */
    PAUSING,
    /**
     * The virtual machine is paused, that is CPU execution has stopped, but the memory image is still loaded.
     */
    PAUSED,
    /**
     * The virtual machine is in the process of transitioning to the {@link #SUSPENDED} state
     */
    SUSPENDING,
    /**
     * The virtual machine is suspended, that is CPU execution has stopped and the memory image has been written to
     * disk.
     */
    SUSPENDED,
    /**
     * The virtual machine is in th process of transitioning from the {@link #SUSPENDED} state to either the
     * {@link #PAUSED} or the {@link #RUNNING} state.
     */
    RESUMING,
    /**
     * The virtual machine is in the process of moving from one {@link Host} to another.
     */
    MIGRATING,
}
