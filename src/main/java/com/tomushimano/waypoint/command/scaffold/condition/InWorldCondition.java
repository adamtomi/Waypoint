package com.tomushimano.waypoint.command.scaffold.condition;

import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import com.tomushimano.waypoint.core.Waypoint;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import grapefruit.command.argument.condition.CommandCondition;
import grapefruit.command.argument.condition.UnfulfilledConditionException;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.util.key.Key;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InWorldCondition implements CommandCondition<CommandSender> {
    private final MessageConfig messageConfig;
    private final Key<Waypoint> waypointKey;

    @AssistedInject
    public InWorldCondition(final MessageConfig messageConfig, final @Assisted Key<Waypoint> waypointKey) {
        this.messageConfig = messageConfig;
        this.waypointKey = waypointKey;
    }

    @Override
    public void test(final CommandContext<CommandSender> context) throws UnfulfilledConditionException {
        final Waypoint waypoint = context.require(this.waypointKey);
        // Assume sender to be a player
        final Player player = (Player) context.source();
        final String expectedWorldName = waypoint.getPosition().getWorldName();

        if (!expectedWorldName.equals(player.getWorld().getName())) {
            throw new VerboseConditionException(this, this.messageConfig.get(MessageKeys.Waypoint.WORLD_ERROR)
                    .with(Placeholder.of("name", expectedWorldName))
                    .make());
        }
    }

    @AssistedFactory
    public interface Factory {

        InWorldCondition create(final Key<Waypoint> waypointKey);
    }
}
