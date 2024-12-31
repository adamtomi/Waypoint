package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.core.WaypointService;
import com.tomushimano.waypoint.message.Messages;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import grapefruit.command.CommandModule;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.slf4j.Logger;

import static com.tomushimano.waypoint.util.ExceptionUtil.capture;

abstract class UpdateWaypointCommand implements CommandModule<CommandSender> {
    private static final Logger LOGGER = NamespacedLoggerFactory.create(UpdateWaypointCommand.class);
    private final WaypointService waypointService;
    private final Configurable config;

    protected UpdateWaypointCommand(final WaypointService waypointService, final Configurable config) {
        this.waypointService = waypointService;
        this.config = config;
    }

    protected void updateAndReport(final Player sender, final Waypoint waypoint) {
        this.waypointService.updateWaypoint(waypoint)
                .thenRun(() -> Messages.WAYPOINT__UPDATE_SUCCESS.from(this.config, waypoint).print(sender))
                .exceptionally(capture(() -> Messages.WAYPOINT__UPDATE_FAILURE.from(this.config).print(sender), "Failed to update waypoint", LOGGER));
    }
}
