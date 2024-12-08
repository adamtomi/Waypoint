package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.core.WaypointService;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import grapefruit.command.CommandModule;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.slf4j.Logger;

import static com.tomushimano.waypoint.util.ExceptionUtil.capture;

abstract class UpdateWaypointCommand implements CommandModule<CommandSender> {
    private static final Logger LOGGER = NamespacedLoggerFactory.create(UpdateWaypointCommand.class);
    private final WaypointService waypointService;
    private final MessageConfig messageConfig;

    protected UpdateWaypointCommand(final WaypointService waypointService, final MessageConfig messageConfig) {
        this.waypointService = waypointService;
        this.messageConfig = messageConfig;
    }

    protected void updateAndReport(final Player sender, final Waypoint waypoint) {
        this.waypointService.updateWaypoint(waypoint)
                .thenApply(x -> this.messageConfig.get(MessageKeys.Waypoint.UPDATE_SUCCESS)
                        .with(Placeholder.of("name", waypoint.getName()))
                        .make())
                .thenAccept(sender::sendMessage)
                .exceptionally(capture(sender, this.messageConfig.get(MessageKeys.Waypoint.UPDATE_FAILURE).make(), "Failed to update waypoint", LOGGER));
    }
}
