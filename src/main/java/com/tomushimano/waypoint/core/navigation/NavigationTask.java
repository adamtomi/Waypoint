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
    private static final int UNIT = 10;
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

        final double xDiff = destination.getX() - origin.getX();
        final double zDiff = destination.getZ() - origin.getZ();

        final int xResolution = (int) ((Math.abs(xDiff) / UNIT) + 1); // * 5
        final int zResolution = (int) ((Math.abs(zDiff) / UNIT) + 1); // * 5

        final int resolution = Math.max(xResolution, zResolution);

        final double xUnit = xDiff / resolution;
        final double zUnit = zDiff / resolution;

        final int baseDistance = this.config.get(StandardKeys.Navigation.PARTICLE_DISTANCE);
        final double a = xUnit / baseDistance * -1;
        final double b = zUnit / baseDistance * -1;
        final double x = xUnit * a + origin.getX();
        final double z = zUnit * b + origin.getZ();
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
            final ParticleConfig config = ParticleConfig.from(this.config);

            // Exit loop if the player is close the targeted waypoint
            while (
                    this.destination.distance(this.player) > this.config.get(StandardKeys.Navigation.ARRIVAL_DISTANCE)
                    && this.running
            ) {
                if (this.updateRequired.get()) {
                    this.updateRequired.set(false);
                    update();
                }

                final Location current = this.player.getLocation();
                // Recalculate next location if necessary
                if (current.distance(this.nextLocation) <= this.config.get(StandardKeys.Navigation.ARRIVED_AT_INDICATOR)) {
                    update();
                }

                this.player.sendActionBar(this.messageConfig.get(MessageKeys.Navigation.DISTANCE_INDICATOR)
                        .with(Placeholder.of("blocks", this.destination.distance(this.player)))
                        .make());

                final Location origin = this.nextLocation;
                final int maxY = origin.getBlockY() + config.extraHeight();
                final Particle.DustOptions options = new Particle.DustOptions(config.color(), config.size());

                for (int y = maxY; y > origin.getBlockY(); y -= config.density()) {
                    final Location loc = new Location(origin.getWorld(), origin.getX(), y, origin.getZ());
                    this.player.spawnParticle(Particle.DUST, loc, config.count(), options);
                    Thread.sleep(10L);
                }

                Thread.sleep(100L);
            }

            this.callback.run();
        } catch (final InterruptedException ignored) {}
    }
}
