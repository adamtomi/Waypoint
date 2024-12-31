package com.tomushimano.waypoint.command.scaffold.condition;

import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.di.qualifier.Lang;
import com.tomushimano.waypoint.message.Messages;
import grapefruit.command.argument.condition.CommandCondition;
import grapefruit.command.argument.condition.UnfulfilledConditionException;
import grapefruit.command.dispatcher.CommandContext;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class IsPlayerCondition implements CommandCondition<CommandSender> {
    private final Configurable config;

    @Inject
    public IsPlayerCondition(final @Lang Configurable config) {
        this.config = config;
    }

    @Override
    public void test(final CommandContext<CommandSender> context) throws UnfulfilledConditionException {
        if (!(context.source() instanceof Player)) {
            throw new VerboseConditionException(this, Messages.COMMAND__NEED_TO_BE_A_PLAYER.from(this.config).comp());
        }
    }
}
