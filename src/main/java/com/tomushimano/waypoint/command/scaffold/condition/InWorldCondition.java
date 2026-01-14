package com.tomushimano.waypoint.command.scaffold.condition;

import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.di.qualifier.Lang;
import com.tomushimano.waypoint.message.Messages;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import grapefruit.command.argument.condition.CommandCondition;
import grapefruit.command.argument.condition.UnfulfilledConditionException;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.util.key.Key;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InWorldCondition implements CommandCondition.Late<CommandSender> {
    private final Configurable config;
    private final Key<Waypoint> waypointKey;

    @AssistedInject
    public InWorldCondition(final @Lang Configurable config, final @Assisted Key<Waypoint> waypointKey) {
        this.config = config;
        this.waypointKey = waypointKey;
    }

    @Override
    public void testLate(final CommandContext<CommandSender> context) throws UnfulfilledConditionException {
        final Waypoint waypoint = context.require(this.waypointKey);
        // Assume sender to be a player
        final Player player = (Player) context.source();
        final String expectedWorldName = waypoint.getPosition().getWorldName();

        if (!expectedWorldName.equals(player.getWorld().getName())) {
            throw new VerboseConditionException(this, Messages.WAYPOINT__WORLD_ERROR.from(this.config, waypoint).comp());
        }
    }

    @AssistedFactory
    public interface Factory {

        InWorldCondition create(final Key<Waypoint> waypointKey);
    }
}
