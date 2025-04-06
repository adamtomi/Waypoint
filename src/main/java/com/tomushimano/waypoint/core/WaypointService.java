package com.tomushimano.waypoint.core;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.tomushimano.waypoint.datastore.StorageHolder;
import com.tomushimano.waypoint.util.Position;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Singleton
public class WaypointService {
    /* Map a set of waypoints to the ownerId */
    private final Multimap<UUID, Waypoint> waypoints = HashMultimap.create();
    private final Waypoint.Factory waypointFactory;
    private final StorageHolder storageHolder;

    @Inject
    public WaypointService(final Waypoint.Factory waypointFactory, final StorageHolder storageHolder) {
        this.waypointFactory = waypointFactory;
        this.storageHolder = storageHolder;
    }

    public Set<Waypoint> getAccessibleWaypoints(final Player player) {
        return this.waypoints.entries()
                .stream()
                .filter(x -> x.getKey().equals(player.getUniqueId()) || x.getValue().isGlobal())
                .map(Map.Entry::getValue)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<Waypoint> getOwnedWaypoints(final Player player) {
        return Set.copyOf(this.waypoints.get(player.getUniqueId()));
    }

    public CompletableFuture<Waypoint> createWaypoint(
            final Player player,
            final String name,
            final @Nullable TextColor color,
            final boolean global
    ) {
        final UUID uniqueId = UUID.randomUUID();
        final UUID ownerId = player.getUniqueId();
        final Waypoint waypoint = this.waypointFactory.create(uniqueId, ownerId, name, color, global, Position.from(player.getLocation()));
        this.waypoints.put(ownerId, waypoint);
        renderForTargets(waypoint);

        return this.storageHolder.get().save(waypoint).thenApply(x -> waypoint);
    }

    public CompletableFuture<?> removeWaypoint(final Waypoint waypoint) {
        hideFromTargets(waypoint);
        this.waypoints.remove(waypoint.getOwnerId(), waypoint);
        return this.storageHolder.get().remove(waypoint);
    }

    public CompletableFuture<?> updateWaypoint(final Waypoint waypoint) {
        return this.storageHolder.get().save(waypoint)
                .thenRun(() -> rerenderForTargets(waypoint));
    }

    public CompletableFuture<?> loadWaypoints(final Player player) {
        return this.storageHolder.get().loadAccessible(player.getUniqueId())
                .thenAccept(x -> {
                    for (Waypoint each : x) {
                        this.waypoints.put(each.getOwnerId(), each);
                        if (each.getPosition().getWorldName().equals(player.getWorld().getName())) {
                            each.render(player);
                        }
                    }
                });
    }

    public void unloadWaypoints(final Player player) {
        final Set<Waypoint> markedForRemoval = new HashSet<>();
        final boolean offline = Bukkit.getOnlinePlayers().size() == 1; // 1, because our player is still online
        for (final Waypoint waypoint : this.waypoints.get(player.getUniqueId())) {
            if (!waypoint.isGlobal() || offline) {
                hideFromTargets(waypoint);
                markedForRemoval.add(waypoint);
            }
        }

        markedForRemoval.forEach(x -> this.waypoints.remove(x.getOwnerId(), x));
    }

    public Optional<Waypoint> getByName(final Player player, final String name) {
        return this.waypoints.entries().stream()
                .map(Map.Entry::getValue)
                .filter(x -> x.getName().equalsIgnoreCase(name) && x.getOwnerId().equals(player.getUniqueId()))
                .findFirst();
    }

    public void handleWorldChange(final Player player, final World from) {
        // Collect accessible waypoints
        final Set<Waypoint> accessibleWaypoints = getAccessibleWaypoints(player);
        for (final Waypoint waypoint : accessibleWaypoints) {
            final Position position = waypoint.getPosition();

            // If the waypoint is in the old world, we hide it
            if (position.getWorldName().equals(from.getName())) {
                waypoint.hide(player);
            } else if (position.getWorldName().equals(player.getWorld().getName())) {
                // If it is in the new world, we render it
                waypoint.render(player);
            }
        }
    }

    public Set<Waypoint> getLoadedWaypoints() {
        return Set.copyOf(this.waypoints.values());
    }

    /* Render the waypoint for its owner, or everyone if the waypoint is global */
    public void renderForTargets(final Waypoint waypoint) {
        runWaypointAction(waypoint, waypoint::render);
    }

    public void rerenderForTargets(final Waypoint waypoint) {
        runWaypointAction(waypoint, waypoint::rerender);
    }

    /* Hide the waypoint from its owner, or everyone if the waypoint is global */
    public void hideFromTargets(final Waypoint waypoint) {
        runWaypointAction(waypoint, waypoint::hide);
    }

    private void runWaypointAction(final Waypoint waypoint, final Consumer<Player> action) {
        if (waypoint.isGlobal()) {
            Bukkit.getOnlinePlayers().forEach(action);
        } else {
            final Player owner = Bukkit.getPlayer(waypoint.getOwnerId());
            if (owner == null) {
                throw new IllegalStateException("Attempted to render a non-global waypoint, but its owner is offline.");
            }

            action.accept(owner);
        }
    }
}
