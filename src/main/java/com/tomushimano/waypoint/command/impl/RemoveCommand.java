package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.command.scaffold.mapper.WaypointArgumentMapper;
import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.core.WaypointService;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import grapefruit.command.CommandModule;
import grapefruit.command.argument.CommandArgument;
import grapefruit.command.argument.CommandChain;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.util.key.Key;
import org.bukkit.command.CommandSender;
import org.slf4j.Logger;

import javax.inject.Inject;

import static com.tomushimano.waypoint.util.ExceptionUtil.capture;
import static grapefruit.command.argument.CommandArgument.literal;

public class RemoveCommand implements CommandModule<CommandSender> {
    private static final Logger LOGGER = NamespacedLoggerFactory.create(RemoveCommand.class);
    private static final Key<Waypoint> WAYPOINT_KEY = Key.named(Waypoint.class, "waypoint");
    private final WaypointArgumentMapper.Provider waypointArgumentMapperProvider;
    private final WaypointService waypointService;
    private final MessageConfig messageConfig;

    @Inject
    public RemoveCommand(
            final WaypointArgumentMapper.Provider waypointArgumentMapperProvider,
            final WaypointService waypointService,
            final MessageConfig messageConfig
    ) {
        this.waypointArgumentMapperProvider = waypointArgumentMapperProvider;
        this.waypointService = waypointService;
        this.messageConfig = messageConfig;
    }

    @Override
    public CommandChain<CommandSender> chain() {
        return CommandChain.<CommandSender>begin()
                .then(literal("waypoint").aliases("wp").build())
                .then(literal("remove").aliases("rm").build())
                .arguments()
                .then(CommandArgument.<CommandSender, Waypoint>required(WAYPOINT_KEY).mapWith(this.waypointArgumentMapperProvider.owning()).build())
                .build();
    }

    @Override
    public void execute(final CommandContext<CommandSender> context) {
        final Waypoint waypoint = context.require(WAYPOINT_KEY);
        final CommandSender sender = context.source();

        this.waypointService.removeWaypoint(waypoint)
                .exceptionally(capture(sender, this.messageConfig.get(MessageKeys.Waypoint.DELETION_FAILURE).make(), "Failed to remove waypoint", LOGGER))
                .thenApply(x -> this.messageConfig.get(MessageKeys.Waypoint.DELETION_SUCCESS)
                        .with(Placeholder.of("name", waypoint.getName()))
                        .make())
                .thenAccept(sender::sendMessage);
    }
}
