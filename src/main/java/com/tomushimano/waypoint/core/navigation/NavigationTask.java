package com.tomushimano.waypoint.core.navigation;

import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import com.tomushimano.waypoint.core.Waypoint;
import org.bukkit.entity.Player;

import static java.util.Objects.requireNonNull;

public class NavigationTask implements Runnable {
    private final Player player;
    private final Waypoint destination;
    private final MessageConfig messageConfig;

    public NavigationTask(
            final Player player,
            final Waypoint destination,
            final MessageConfig messageConfig
    ) {
        this.player = requireNonNull(player, "player cannot be null");
        this.destination = requireNonNull(destination, "destination cannot be null");
        this.messageConfig = requireNonNull(messageConfig, "messageConfig cannot be null");
    }

    @Override
    public void run() {
        try {
            while (true) {
                this.player.sendActionBar(this.messageConfig.get(MessageKeys.Navigation.DISTANCE_INDICATOR)
                        .with(Placeholder.of("blocks", this.destination.distance(this.player)))
                        .make());
                Thread.sleep(1000L);
            }
        } catch (final InterruptedException ex) {

        }
    }
}
