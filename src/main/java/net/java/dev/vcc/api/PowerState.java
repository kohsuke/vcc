package net.java.dev.vcc.api;

/**
 * Represents the power states of a virtual machine.
 */
public enum PowerState {
    /**
     * The virtual machine has been powered off, to power it on again will
     * require going through the BIOS boot sequence.
     */
    STOPPED,

    /**
     * A running virtual machine's state has been written out to disk, powering it
     * on again will resume it from this state and will be delayed while the state
     * is restored from disk.
     */
    SUSPENDED,

    /**
     * Execution of a running virtual machine has been paused, the state is
     * still in memory but it is not consuming CPU cycles.
     */
    PAUSED,

    /**
     * The virtual machine's state is in memory and active.
     */
    RUNNING,
}
