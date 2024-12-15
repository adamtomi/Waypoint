package com.tomushimano.waypoint.command.scaffold;

import com.tomushimano.waypoint.util.VerboseException;
import grapefruit.command.CommandException;
import net.kyori.adventure.text.Component;

import java.io.Serial;

import static java.util.Objects.requireNonNull;

public class VerboseArgumentException extends CommandException implements VerboseException {
    @Serial
    private static final long serialVersionUID = 2861564508491967866L;
    private final Component richMessage;

    public VerboseArgumentException(final Component richMessage) {
        this.richMessage = requireNonNull(richMessage, "richMessage cannot be null");
    }

    @Override
    public Component describeFailure() {
        return this.richMessage;
    }
}
