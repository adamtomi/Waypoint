package com.tomushimano.waypoint.util;

import org.bukkit.Location;

import static java.util.Objects.requireNonNull;

public class Position {
    private final String worldName;
    private final double x;
    private final double y;
    private final double z;

    public Position(String worldName, double x, double y, double z) {
        this.worldName = requireNonNull(worldName, "worldName cannot be null");
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Position from(Location location) {
        requireNonNull(location, "location cannot be null");
        return new Position(
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ()
        );
    }

    public String getWorldName() {
        return this.worldName;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public Position plus(double x, double y, double z) {
        return new Position(this.worldName, this.x + x, this.y + y, this.z + z);
    }
}
