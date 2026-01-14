package com.tomushimano.waypoint.command.scaffold.condition;

import com.tomushimano.waypoint.config.Configurable;
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
    // private final Component richMessage;
    private final ComponentProvider provider;

    /*
    public VerboseConditionException(final CommandCondition<CommandSender> condition, final Component richMessage) {
        super(condition);
        this.richMessage = requireNonNull(richMessage, "richMessage cannot be null");
    }
     */

    public VerboseConditionException(final CommandCondition<CommandSender> condition, final ComponentProvider provider) {
        super(condition);
        this.provider = requireNonNull(provider, "provider cannot be null");
    }

    @Override
    public Component describeFailure(final Configurable config) {
        return this.provider.provide(config);
    }
}
