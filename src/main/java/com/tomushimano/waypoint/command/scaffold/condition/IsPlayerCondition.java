package com.tomushimano.waypoint.command.scaffold.condition;

import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import grapefruit.command.argument.condition.CommandCondition;
import grapefruit.command.argument.condition.UnfulfilledConditionException;
import grapefruit.command.dispatcher.CommandContext;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class IsPlayerCondition implements CommandCondition<CommandSender> {
    private final MessageConfig messageConfig;

    @Inject
    public IsPlayerCondition(final MessageConfig messageConfig) {
        this.messageConfig = messageConfig;
    }

    @Override
    public void test(final CommandContext<CommandSender> context) throws UnfulfilledConditionException {
        if (!(context.source() instanceof Player)) {
            throw new VerboseConditionException(this, this.messageConfig.get(MessageKeys.Command.NEED_TO_BE_A_PLAYER).make());
        }
    }
}
