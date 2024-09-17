package com.tomushimano.waypoint.command.scaffold.condition;

import grapefruit.command.dispatcher.CommandContext;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import static com.tomushimano.waypoint.command.scaffold.WaypointContextKeys.SENDER_KEY;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class IsPlayer implements VerboseCondition {

    @Override
    public Component describeFailure() {
        return text("You need to be a player to execute this command.", RED);
    }

    @Override
    public boolean evaluate(CommandContext context) {
        return context.require(SENDER_KEY) instanceof Player;
    }
}
