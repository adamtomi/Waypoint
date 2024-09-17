package com.tomushimano.waypoint.core;

import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class WaypointService {

    @Inject
    public WaypointService() {}

    public void loadWaypointsInWorld(World world) {}

    public Set<Waypoint> getAccessibleWaypoints(Player player) {
        return Set.of();
    }

    public CompletableFuture<Waypoint> createWaypoint(String name, String displayName, boolean global) {
        return new CompletableFuture<>();
    }
}
