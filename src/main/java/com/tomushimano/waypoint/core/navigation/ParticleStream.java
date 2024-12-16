package com.tomushimano.waypoint.core.navigation;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class ParticleStream {
    private final List<Location> locations;
    private final Supplier<Particle.DustOptions> dustOptionsSupplier;
    private final Runnable finishHook;
    private final Thread thread;

    private ParticleStream(
            final List<Location> locations,
            final Supplier<Particle.DustOptions> dustOptionsSupplier,
            final Runnable finishHook
    ) {
        this.locations = requireNonNull(locations, "locations canont be null");
        this.dustOptionsSupplier = requireNonNull(dustOptionsSupplier, "dustOptionsSupplier cannot be null");
        this.finishHook = requireNonNull(finishHook, "finishHook cannot be null");
        this.thread = Thread.currentThread();
    }

    public static ParticleStream init(
            final Location origin,
            final Location destination,
            final Supplier<Particle.DustOptions> dustOptionsSupplier,
            final Runnable finishHook
    ) {
        final int count = 15;

        final double xDiff = Math.min(destination.getX() - origin.getX(), count);
        final double zDiff = Math.min(destination.getZ() - origin.getZ(), count);

        final double xUnit = xDiff / count;
        final double zUnit = zDiff / count;

        final List<Location> locations = new ArrayList<>(count);
        for (int i = 0; i <= count; i++) {
            locations.add(origin.clone().add(i * xUnit, 0, i * zUnit));
        }

        return new ParticleStream(locations, dustOptionsSupplier, finishHook);
    }

    public void cancel() {
        this.thread.interrupt();
        this.locations.clear();
    }

    public void play(final Player player, final int count) {
        try {
            for (int i = 0; i < count; i++) {
                for (final Location location : this.locations) {
                    player.spawnParticle(Particle.DUST, location, 5, this.dustOptionsSupplier.get());
                    Thread.sleep(100L);
                }

                Thread.sleep(10L);
            }

            this.locations.clear();
            this.finishHook.run();
        } catch (final InterruptedException ignored) {}
    }
}
