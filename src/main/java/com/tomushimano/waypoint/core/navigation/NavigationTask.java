package com.tomushimano.waypoint.core.navigation;

import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.config.StandardKeys;
import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import com.tomushimano.waypoint.core.Waypoint;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Objects.requireNonNull;

public final class NavigationTask implements Runnable {
    private final AtomicBoolean updateRequired = new AtomicBoolean(false);
    private final UUID uniqueId;
    private final Player player;
    private final Waypoint destination;
    private final MessageConfig messageConfig;
    private final Configurable config;
    private final Runnable callback;
    private Location nextLocation;
    private boolean running = true;
    private Thread thread;

    public NavigationTask(
            final UUID uniqueId,
            final Player player,
            final Waypoint destination,
            final MessageConfig messageConfig,
            final Configurable config,
            final Runnable callback
    ) {
        this.uniqueId = requireNonNull(uniqueId, "uniqueId cannot be null");
        this.player = requireNonNull(player, "player cannot be null");
        this.destination = requireNonNull(destination, "destination cannot be null");
        this.messageConfig = requireNonNull(messageConfig, "messageConfig cannot be null");
        this.config = requireNonNull(config, "config cannot be null");
        this.callback = requireNonNull(callback, "callback cannot be null");
        update();
    }

    public Waypoint destination() {
        return this.destination;
    }

    public UUID uniqueId() {
        return this.uniqueId;
    }

    public void cancel() {
        this.running = false;
        if (this.thread != null) this.thread.interrupt();
    }

    public void queueUpdate() {
        this.updateRequired.set(true);
    }

    private void update() {
        final Location origin = this.player.getLocation();
        final Location destination = this.destination.getPosition().toLocation();
        final int distanceMultiplier = this.config.get(StandardKeys.Navigation.INDICATOR_DISTANCE_MULTIPLIER);

        final double xDiff = destination.getX() - origin.getX();
        final double zDiff = destination.getZ() - origin.getZ();

        final int resolution = (int) Math.max(Math.abs(xDiff), Math.abs(zDiff));

        final double xUnit = xDiff / resolution;
        final double zUnit = zDiff / resolution;

        final double x = xUnit * distanceMultiplier + origin.getX();
        final double z = zUnit * distanceMultiplier + origin.getZ();
        final int y = origin.getWorld().getHighestBlockYAt((int) x, (int) z);

        this.nextLocation = new Location(
                origin.getWorld(),
                x,
                y,
                z
        );
    }

    @Override
    public void run() {
        try {
            this.thread = Thread.currentThread();
            // Exit loop if the player is close the targeted waypoint
            while (
                    this.destination.distance(this.player) > this.config.get(StandardKeys.Navigation.ARRIVAL_DISTANCE)
                    && this.running
            ) {
                final Location current = this.player.getLocation();
                final double distance = current.distance(this.nextLocation);
                // Recalculate next location if necessary
                if (
                        this.updateRequired.get()
                        || distance <= this.config.get(StandardKeys.Navigation.INDICATOR_ARRIVAL_DISTANCE)
                        || distance >= this.config.get(StandardKeys.Navigation.INDICATOR_MAX_DISTANCE)
                ) {
                    this.updateRequired.set(false);
                    update();
                }

                this.player.sendActionBar(this.messageConfig.get(MessageKeys.Navigation.DISTANCE_INDICATOR)
                        .with(Placeholder.of("blocks", this.destination.distance(this.player)))
                        .make());

                final Location origin = this.nextLocation;
                final int maxY = origin.getBlockY() + this.config.get(StandardKeys.Navigation.PARTICLE_Y_OFFSET);
                final Particle.DustOptions options = new Particle.DustOptions(
                        this.config.get(StandardKeys.Navigation.PARTICLE_COLOR),
                        this.config.get(StandardKeys.Navigation.PARTICLE_SIZE)
                );

                for (int y = maxY; y > origin.getBlockY(); y -= this.config.get(StandardKeys.Navigation.PARTICLE_DENSITY)) {
                    final Location loc = new Location(origin.getWorld(), origin.getX(), y, origin.getZ());
                    this.player.spawnParticle(Particle.DUST, loc, this.config.get(StandardKeys.Navigation.PARTICLE_COUNT), options);
                    Thread.sleep(10L);
                }

                Thread.sleep(100L);
            }

            this.callback.run();
        } catch (final InterruptedException ignored) {}
    }
}
