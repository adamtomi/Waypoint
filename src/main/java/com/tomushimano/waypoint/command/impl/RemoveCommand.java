package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.command.scaffold.mapper.WaypointArgumentMapper;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.core.WaypointService;
import com.tomushimano.waypoint.core.navigation.NavigationService;
import com.tomushimano.waypoint.di.qualifier.Lang;
import com.tomushimano.waypoint.di.qualifier.Own;
import com.tomushimano.waypoint.message.Messages;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import grapefruit.command.CommandModule;
import grapefruit.command.argument.CommandChain;
import grapefruit.command.argument.CommandChainFactory;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.util.key.Key;
import org.bukkit.command.CommandSender;
import org.slf4j.Logger;

import javax.inject.Inject;

import static com.tomushimano.waypoint.command.scaffold.condition.IsPlayerCondition.isPlayer;
import static com.tomushimano.waypoint.command.scaffold.condition.PermissionCondition.perm;
import static com.tomushimano.waypoint.util.ExceptionUtil.capture;
import static grapefruit.command.argument.condition.CommandCondition.and;

public class RemoveCommand implements CommandModule<CommandSender> {
    private static final Logger LOGGER = NamespacedLoggerFactory.create(RemoveCommand.class);
    private static final Key<Waypoint> WAYPOINT_KEY = Key.named(Waypoint.class, "waypoint");
    private final WaypointService waypointService;
    private final NavigationService navigationService;
    private final Configurable config;
    private final WaypointArgumentMapper waypointMapper;

    @Inject
    public RemoveCommand(
            final WaypointService waypointService,
            final NavigationService navigationService,
            final @Lang Configurable config,
            final @Own WaypointArgumentMapper waypointMapper
    ) {
        this.waypointService = waypointService;
        this.navigationService = navigationService;
        this.config = config;
        this.waypointMapper = waypointMapper;
    }

    @Override
    public CommandChain<CommandSender> chain(final CommandChainFactory<CommandSender> factory) {
        return factory.newChain()
                .then(factory.literal("waypoint").aliases("wp").build())
                .then(factory.literal("remove").aliases("rm").expect(and(perm("waypoint.remove"), isPlayer())).build())
                .arguments()
                .then(factory.required(WAYPOINT_KEY).mapWith(this.waypointMapper).build())
                .build();
    }

    @Override
    public void execute(final CommandContext<CommandSender> context) {
        final Waypoint waypoint = context.require(WAYPOINT_KEY);
        final CommandSender sender = context.source();

        this.waypointService.removeWaypoint(waypoint)
                .thenRun(() -> this.navigationService.cancelAll(waypoint))
                .thenRun(() -> Messages.WAYPOINT__DELETION_SUCCESS.from(this.config, waypoint).print(sender))
                .exceptionally(capture(() -> Messages.WAYPOINT__DELETION_FAILURE.from(this.config).print(sender), "Failed to remove waypoint", LOGGER));
    }
}
