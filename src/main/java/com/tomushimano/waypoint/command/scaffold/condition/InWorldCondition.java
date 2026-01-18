package com.tomushimano.waypoint.command.scaffold.condition;

import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.message.Messages;
import grapefruit.command.argument.condition.CommandCondition;
import grapefruit.command.argument.condition.UnfulfilledConditionException;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.util.key.Key;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static java.util.Objects.requireNonNull;

public class InWorldCondition implements CommandCondition.Late<CommandSender> {
    private final Key<Waypoint> waypointKey;

    private InWorldCondition(final Key<Waypoint> waypointKey) {
        this.waypointKey = requireNonNull(waypointKey, "waypointKey cannot be null");
    }

    public static InWorldCondition inWorld(final Key<Waypoint> waypointKey) {
        return new InWorldCondition(waypointKey);
    }

    @Override
    public void testLate(final CommandContext<CommandSender> context) throws UnfulfilledConditionException {
        final Waypoint waypoint = context.require(this.waypointKey);
        // Assume sender to be a player
        final Player player = (Player) context.source();
        final String expectedWorldName = waypoint.getPosition().getWorldName();

        if (!expectedWorldName.equals(player.getWorld().getName())) {
            throw new VerboseConditionException(this, config -> Messages.WAYPOINT__WORLD_ERROR.from(config, waypoint).comp());
        }
    }
}
