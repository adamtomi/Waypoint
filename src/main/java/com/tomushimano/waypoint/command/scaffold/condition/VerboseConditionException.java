package com.tomushimano.waypoint.command.scaffold.condition;

import com.tomushimano.waypoint.util.VerboseException;
import grapefruit.command.argument.condition.CommandCondition;
import grapefruit.command.argument.condition.UnfulfilledConditionException;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.io.Serial;

import static java.util.Objects.requireNonNull;

public class VerboseConditionException extends UnfulfilledConditionException implements VerboseException {
    @Serial
    private static final long serialVersionUID = 8898193163773950125L;
    private final Component richMessage;

    public VerboseConditionException(final CommandCondition<CommandSender> condition, final Component richMessage) {
        super(condition);
        this.richMessage = requireNonNull(richMessage, "richMessage cannot be null");
    }

    @Override
    public Component describeFailure() {
        return this.richMessage;
    }
}
