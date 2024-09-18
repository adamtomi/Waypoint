package com.tomushimano.waypoint.core.listener;

import com.tomushimano.waypoint.core.WaypointService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import javax.inject.Inject;

public class WorldEventListener implements Listener {
    private final WaypointService waypointService;

    @Inject
    public WorldEventListener(WaypointService waypointService) {
        this.waypointService = waypointService;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        this.waypointService.loadWaypointsInWorld(event.getWorld());
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        this.waypointService.unloadWaypointsInWorld(event.getWorld());
    }
}
