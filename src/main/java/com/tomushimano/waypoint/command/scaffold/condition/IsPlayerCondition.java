package com.tomushimano.waypoint.command.scaffold.condition;

import com.tomushimano.waypoint.message.Messages;
import grapefruit.command.argument.condition.CommandCondition;
import grapefruit.command.argument.condition.UnfulfilledConditionException;
import grapefruit.command.dispatcher.CommandContext;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IsPlayerCondition implements CommandCondition.Early<CommandSender> {
    private static final IsPlayerCondition INSTANCE = new IsPlayerCondition();

    private IsPlayerCondition() {}

    public static IsPlayerCondition isPlayer() {
        return INSTANCE;
    }

    @Override
    public void testEarly(final CommandContext<CommandSender> context) throws UnfulfilledConditionException {
        if (!(context.source() instanceof Player)) {
            throw new VerboseConditionException(this, config -> Messages.COMMAND__NEED_TO_BE_A_PLAYER.from(config).comp());
        }
    }
}
