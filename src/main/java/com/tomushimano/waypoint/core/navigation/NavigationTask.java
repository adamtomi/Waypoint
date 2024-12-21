package com.tomushimano.waypoint.core.navigation;

import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import com.tomushimano.waypoint.core.Waypoint;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class NavigationTask implements Runnable {
    private static final int UNIT = 10;
    private final Player player;
    private final Waypoint destination;
    private final MessageConfig messageConfig;
    private final Location loc;
    private final ParticleStream stream;

    public NavigationTask(
            final Player player,
            final Waypoint destination,
            final MessageConfig messageConfig,
            final ParticleStream stream
    ) {
        this.player = requireNonNull(player, "player cannot be null");
        this.destination = requireNonNull(destination, "destination cannot be null");
        this.messageConfig = requireNonNull(messageConfig, "messageConfig cannot be null");
        this.stream = requireNonNull(stream, "stream cannot be null");
        this.loc = nextWaypoint();
    }

    public Location nextWaypoint() {
        final Location origin = this.player.getLocation();
        final Location destination = this.destination.getPosition().toLocation();

        final double xDiff = destination.getX() - origin.getX();
        final double zDiff = destination.getZ() - origin.getZ();

        final int xResolution = (int) ((Math.abs(xDiff) / UNIT) + 1) * 5;
        final int zResolution = (int) ((Math.abs(zDiff) / UNIT) + 1) * 5;

        final int resolution = Math.max(xResolution, zResolution);

        final double xUnit = xDiff / resolution;
        final double zUnit = zDiff / resolution;

        final double x = xUnit * 10 + origin.getX();
        final double z = zUnit * 10 + origin.getZ();
        final int y = origin.getWorld().getHighestBlockYAt((int) x, (int) z);

        return new Location(
                origin.getWorld(),
                x,
                y,
                z
        );
    }

    @Override
    public void run() {
        try {
            while (true) {
                this.player.sendActionBar(this.messageConfig.get(MessageKeys.Navigation.DISTANCE_INDICATOR)
                        .with(Placeholder.of("blocks", this.destination.distance(this.player)))
                        .make());

                final Location origin = this.loc;
                final int maxY = origin.getBlockY() + 30;
                for (int y = maxY; y > origin.getBlockY(); y -= 2) {

                    final Location loc = new Location(origin.getWorld(), origin.getX(), y, origin.getZ());
                    this.player.spawnParticle(Particle.DUST, loc, 10, new Particle.DustOptions(Color.fromRGB(58, 201, 48), 5));
                }

                Thread.sleep(1000L);
            }
        } catch (final InterruptedException ignored) {}
    }
}
