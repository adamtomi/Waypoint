package com.tomushimano.waypoint.core.listener;

import com.tomushimano.waypoint.core.WaypointService;
import com.tomushimano.waypoint.core.navigation.NavigationService;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.slf4j.Logger;

import javax.inject.Inject;

import static com.tomushimano.waypoint.util.BukkitUtil.formatPlayer;
import static com.tomushimano.waypoint.util.ExceptionUtil.capture;

public class PlayerEventListener implements Listener {
    private static final Logger LOGGER = NamespacedLoggerFactory.create(PlayerEventListener.class);
    private final WaypointService waypointService;
    private final NavigationService navigationService;

    @Inject
    public PlayerEventListener(final WaypointService waypointService, final NavigationService navigationService) {
        this.waypointService = waypointService;
        this.navigationService = navigationService;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        this.waypointService.loadWaypoints(player)
                .exceptionally(capture("Failed to load the waypoints of %s".formatted(formatPlayer(player)), LOGGER));
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        this.waypointService.unloadWaypoints(player);
        this.navigationService.stopNavigation(player);
    }

    @EventHandler
    public void onWorldChange(final PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();
        final World world = event.getFrom();
        this.waypointService.handleWorldChange(player, world);
        this.navigationService.stopNavigation(player);
    }
}
