package com.tomushimano.waypoint.core.navigation;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class ParticleStream {
    private static final int UNIT = 10;
    private final List<Location> locations;
    private final ParticleConfig config;
    private final Runnable finishHook;
    private final Thread thread;
    private boolean running = true;

    private ParticleStream(
            final List<Location> locations,
            final ParticleConfig config,
            final Runnable finishHook
    ) {
        this.locations = requireNonNull(locations, "locations canont be null");
        this.config = requireNonNull(config, "config canont be null");
        this.finishHook = requireNonNull(finishHook, "finishHook cannot be null");
        this.thread = Thread.currentThread();
    }

    public static ParticleStream init(
            final Location origin,
            final Location destination,
            final ParticleConfig config,
            final Runnable finishHook
    ) {
        final int length = config.length();
        final double xDiff = destination.getX() - origin.getX();
        final double zDiff = destination.getZ() - origin.getZ();

        final double _xUnit = xDiff / length;
        final double _zUnit = zDiff / length;

        final int xResolution = (int) ((Math.abs(xDiff) / UNIT) + 1) * config.density();
        final int zResolution = (int) ((Math.abs(zDiff) / UNIT) + 1) * config.density();

        final int resolution = Math.max(xResolution, zResolution);

        final double xUnit = xDiff / resolution;
        final double zUnit = zDiff / resolution;

        final List<Location> locations = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            locations.add(origin.clone().add(i * xUnit, 0, i * zUnit));
        }

        return new ParticleStream(
                locations,
                config,
                finishHook
        );
    }

    public void cancel() {
        this.running = false;
        this.thread.interrupt();
        this.locations.clear();
    }

    public void play(final Player player) {
        try {
            final int particleCount = config.count();
            final Particle.DustOptions dustOptions = new Particle.DustOptions(this.config.color(), this.config.size());
            while (this.running) {
                for (final Location location : this.locations) {
                    player.spawnParticle(Particle.DUST, location, particleCount, dustOptions);
                    Thread.sleep(30L);
                }

                Thread.sleep(100L);
            }
        } catch (final InterruptedException ignored) {
        } finally {
            this.locations.clear();
            this.finishHook.run();
        }
    }
}
