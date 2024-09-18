package com.tomushimano.waypoint.core.listener;

import com.tomushimano.waypoint.core.WaypointService;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.slf4j.Logger;

import javax.inject.Inject;

import static com.tomushimano.waypoint.util.BukkitUtil.formatPlayer;
import static com.tomushimano.waypoint.util.ExceptionUtil.capture;

public class PlayerEventListener implements Listener {
    private static final Logger LOGGER = NamespacedLoggerFactory.create("PlayerListener");
    private final WaypointService waypointService;

    @Inject
    public PlayerEventListener(WaypointService waypointService) {
        this.waypointService = waypointService;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.waypointService.loadWaypoints(player)
                .exceptionally(capture("Failed to load the waypoints of %s".formatted(formatPlayer(player)), LOGGER));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.waypointService.unloadWaypoints(event.getPlayer());
    }
}
