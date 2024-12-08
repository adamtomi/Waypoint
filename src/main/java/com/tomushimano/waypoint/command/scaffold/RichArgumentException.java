package com.tomushimano.waypoint.command.scaffold;

import grapefruit.command.argument.CommandArgumentException;
import grapefruit.command.dispatcher.input.CommandInputTokenizer;
import net.kyori.adventure.text.Component;

import java.io.Serial;

import static java.util.Objects.requireNonNull;

public class RichArgumentException extends CommandArgumentException {
    @Serial
    private static final long serialVersionUID = 2861564508491967866L;
    private final Component richMessage;

    private RichArgumentException(final String consumed, final String argument, final String remaining, final Component richMessage) {
        super(consumed, argument, remaining);
        this.richMessage = requireNonNull(richMessage, "richMessage cannot be null");
    }

    public static RichArgumentException fromInput(final CommandInputTokenizer input, final String argument, final Component richMessage) {
        return new RichArgumentException(input.consumed(), argument, input.remainingOrEmpty(), richMessage);
    }

    public Component richMessage() {
        return this.richMessage;
    }
}
