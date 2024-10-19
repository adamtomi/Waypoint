package com.tomushimano.waypoint.command.scaffold.condition;

import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import grapefruit.command.runtime.dispatcher.CommandContext;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import javax.inject.Inject;

import static com.tomushimano.waypoint.command.scaffold.WaypointContextKeys.SENDER_KEY;

public class IsPlayer implements VerboseCondition {
    private final MessageConfig messageConfig;

    @Inject
    public IsPlayer(MessageConfig messageConfig) {
        this.messageConfig = messageConfig;
    }

    @Override
    public Component describeFailure() {
        return this.messageConfig.get(MessageKeys.Command.NEED_TO_BE_A_PLAYER).make();
    }

    @Override
    public boolean evaluate(CommandContext context) {
        return context.require(SENDER_KEY) instanceof Player;
    }
}
