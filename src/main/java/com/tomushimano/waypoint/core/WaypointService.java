package com.tomushimano.waypoint.core;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.tomushimano.waypoint.datastore.StorageHolder;
import com.tomushimano.waypoint.util.Position;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Singleton
public class WaypointService {
    private final Multimap<UUID, Waypoint> waypoints = HashMultimap.create();
    private final StorageHolder storageHolder;

    @Inject
    public WaypointService(StorageHolder storageHolder) {
        this.storageHolder = storageHolder;
    }

    public void loadWaypointsInWorld(World world) {

    }

    public void unloadWaypointsInWorld(World world) {

    }

    public Set<Waypoint> getAccessibleWaypoints(Player player) {
        return this.waypoints.entries()
                .stream()
                .filter(x -> x.getKey().equals(player.getUniqueId()) || x.getValue().isGlobal())
                .map(Map.Entry::getValue)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<Waypoint> getOwnedWaypoints(Player player) {
        return Set.copyOf(this.waypoints.get(player.getUniqueId()));
    }

    // TODO render waypoint
    public CompletableFuture<Waypoint> createWaypoint(Player player, String name, boolean global) {
        UUID uniqueId = UUID.randomUUID();
        UUID ownerId = player.getUniqueId();
        Waypoint waypoint = new Waypoint(uniqueId, ownerId, name, global, Position.from(player.getLocation()));
        this.waypoints.put(ownerId, waypoint);
        return this.storageHolder.get().save(waypoint).thenApply(x -> waypoint);
    }

    public CompletableFuture<?> removeWaypoint(Waypoint waypoint) {
        // TODO send entity destroy packet
        this.waypoints.remove(waypoint.getOwnerId(), waypoint);
        return this.storageHolder.get().remove(waypoint);
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

    public Optional<Waypoint> getByName(Player player, String name) {
        return this.waypoints.entries().stream()
                .map(Map.Entry::getValue)
                .filter(x -> x.getName().equalsIgnoreCase(name) && x.getOwnerId().equals(player.getUniqueId()))
                .findFirst();
    }
}
