package com.tomushimano.waypoint.util;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class ParticleStream {
    private final List<Location> locations;
    private final Thread thread;

    private ParticleStream(final List<Location> locations) {
        this.locations = requireNonNull(locations, "locations canont be null");
        this.thread = Thread.currentThread();
    }

    public static ParticleStream create(final Location origin, final Location destination) {
        final int count = 15;

        final double xDiff = Math.min(destination.getX() - origin.getX(), count);
        final double zDiff = Math.min(destination.getZ() - origin.getZ(), count);

        final double xUnit = xDiff / count;
        final double zUnit = zDiff / count;

        final double xOrigin = origin.getX();
        final double zOrigin = origin.getZ();

        System.out.println("------------------------------------");
        System.out.println("%f; %s ==> %f; %f".formatted(xOrigin, zOrigin, destination.getX(), destination.getZ()));

        final List<Location> locations = new ArrayList<>(count);
        for (int i = 0; i <= count; i++) {
            final double x = xOrigin + (i * xUnit);
            final double z = zOrigin + (i * zUnit);

            System.out.println("%f; %f".formatted(x, z));
            locations.add(origin.clone().add(i * xUnit, 0, i * zUnit));
        }

        return new ParticleStream(locations);
    }

    public void cancel() {
        this.thread.interrupt();
        this.locations.clear();
    }

    public void play(final Player player, final int count) {
        try {
            for (int i = 0; i < count; i++) {
                for (final Location location : this.locations) {
                    player.spawnParticle(Particle.DUST, location, 10, new Particle.DustOptions(Color.RED, 5));
                    Thread.sleep(100L);
                }

                Thread.sleep(10L);
            }
        } catch (final InterruptedException ignored) {}
    }
}
