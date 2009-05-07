package net.java.dev.vcc.api.commands;

import net.java.dev.vcc.api.Command;
import net.java.dev.vcc.api.Computer;

/**
 * Takes a {@link net.java.dev.vcc.api.ComputerSnapshot} of a {@link net.java.dev.vcc.api.Computer}.
 */
public final class TakeSnapshot extends Command<Computer> {
    private String name = null;

    /**
     * Returns the suggested name of the snapshot or {@code null} if no name has been suggested.
     *
     * @return the suggested name of the snapshot or {@code null} if no name has been suggested.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the suggested name of the snapshot.
     *
     * @param name the suggested name of the snapshot or {@code null} to indicate that the name of the snapshot is
     *             not important..
     */
    public void setName(String name) {
        checkNotSubmitted();
        this.name = name;
    }
}