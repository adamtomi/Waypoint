package com.tomushimano.waypoint.core;

import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Singleton
public class WaypointService {

    @Inject
    public WaypointService() {}

    public void loadWaypointsInWorld(World world) {

    }

    public void unloadWaypointsInWorld(World world) {

    }

    public Set<Waypoint> getAccessibleWaypoints(Player player) {
        return Set.of();
    }

    public Set<Waypoint> getOwnedWaypoints(Player player) {
        return Set.of();
    }

    public CompletableFuture<Waypoint> createWaypoint(String name, String displayName, boolean global) {
        return new CompletableFuture<>();
    }

    public CompletableFuture<?> removeWaypoint(Waypoint waypoint) {
        return new CompletableFuture<>();
    }

    public CompletableFuture<?> updateWaypoint(Waypoint waypoint) {
        return new CompletableFuture<>();
    }

    public CompletableFuture<?> loadWaypoints(Player player) {
        return new CompletableFuture<>();
    }

    public void unloadWaypoints(Player player) {
        // TODO unload waypoints, except for global ones, unless no one is online, because then unload all
    }
}
