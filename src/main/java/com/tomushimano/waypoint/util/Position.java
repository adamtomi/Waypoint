package com.tomushimano.waypoint.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import static java.util.Objects.requireNonNull;

public class Position {
    private final String worldName;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;

    public Position(String worldName, double x, double y, double z, float yaw, float pitch) {
        this.worldName = requireNonNull(worldName, "worldName cannot be null");
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public static Position from(Location location) {
        requireNonNull(location, "location cannot be null");
        return new Position(
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
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

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public Location toBukkitLocation() {
        return new Location(
                Bukkit.getWorld(this.worldName),
                this.x,
                this.y,
                this.z,
                this.yaw,
                this.pitch
        );
    }
}
