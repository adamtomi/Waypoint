package com.tomushimano.waypoint.command.scaffold;

import grapefruit.command.runtime.argument.CommandArgumentException;
import net.kyori.adventure.text.Component;

import java.io.Serial;

import static java.util.Objects.requireNonNull;

public class RichCommandException extends CommandArgumentException {
    @Serial
    private static final long serialVersionUID = -5861910885488907453L;
    private final Component message;

    public RichCommandException(Component message) {
        super("");
        this.message = requireNonNull(message, "message cannot be null");
    }

    public Component message() {
        return this.message;
    }
}
