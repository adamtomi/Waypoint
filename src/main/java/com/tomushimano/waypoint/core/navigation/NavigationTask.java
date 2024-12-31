package com.tomushimano.waypoint.core.navigation;

import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.config.StandardKeys;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.message.Messages;
import com.tomushimano.waypoint.util.BukkitUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Objects.requireNonNull;

public final class NavigationTask implements Runnable {
    private final AtomicBoolean updateRequired = new AtomicBoolean(false);
    private final UUID uniqueId;
    private final Player player;
    private final Waypoint destination;
    private final Configurable langConfig;
    private final Configurable config;
    private final Runnable callback;
    private Location nextLocation;
    private boolean running = true;
    private Thread thread;

    public NavigationTask(
            final UUID uniqueId,
            final Player player,
            final Waypoint destination,
            final Configurable langConfig,
            final Configurable config,
            final Runnable callback
    ) {
        this.uniqueId = requireNonNull(uniqueId, "uniqueId cannot be null");
        this.player = requireNonNull(player, "player cannot be null");
        this.destination = requireNonNull(destination, "destination cannot be null");
        this.langConfig = requireNonNull(langConfig, "langConfig cannot be null");
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
        final World world = origin.getWorld();
        final int y = BukkitUtil.getSensibleHighestY(world, x, z, this.player);

        this.nextLocation = new Location(
                world,
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
                final double distance = BukkitUtil.distanceIgnoreY(this.player, this.nextLocation);
                // Recalculate next location if necessary
                if (
                        this.updateRequired.get()
                        || distance <= this.config.get(StandardKeys.Navigation.INDICATOR_ARRIVAL_DISTANCE)
                        || distance >= this.config.get(StandardKeys.Navigation.INDICATOR_MAX_DISTANCE)
                ) {
                    this.updateRequired.set(false);
                    update();
                }

                // Update action bar message
                Messages.NAVIGATION__DISTANCE_INDICATOR.from(this.langConfig, this.destination.distance(this.player))
                        .printActionBar(player);

                final Location origin = this.nextLocation;
                final World world = origin.getWorld();
                final int maxY = origin.getBlockY() + this.config.get(StandardKeys.Navigation.PARTICLE_Y_OFFSET);
                final Particle.DustOptions options = new Particle.DustOptions(
                        this.config.get(StandardKeys.Navigation.PARTICLE_COLOR),
                        this.config.get(StandardKeys.Navigation.PARTICLE_SIZE)
                );

                for (int y = maxY; y > origin.getBlockY(); y -= this.config.get(StandardKeys.Navigation.PARTICLE_DENSITY)) {
                    final Location loc = new Location(world, origin.getX(), y, origin.getZ());
                    if (!world.getBlockAt(loc).getType().equals(Material.AIR)) continue;

                    this.player.spawnParticle(Particle.DUST, loc, this.config.get(StandardKeys.Navigation.PARTICLE_COUNT), options);
                    Thread.sleep(10L);
                }

                Thread.sleep(100L);
            }

            this.callback.run();
        } catch (final InterruptedException ignored) {}
    }
}
