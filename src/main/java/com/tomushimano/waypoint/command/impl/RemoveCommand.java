package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.command.scaffold.CommandHelper;
import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.core.WaypointService;
import com.tomushimano.waypoint.core.navigation.NavigationService;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import grapefruit.command.CommandModule;
import grapefruit.command.argument.CommandChain;
import grapefruit.command.argument.CommandChainFactory;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.util.key.Key;
import org.bukkit.command.CommandSender;
import org.slf4j.Logger;

import javax.inject.Inject;

import static com.tomushimano.waypoint.util.ExceptionUtil.capture;
import static grapefruit.command.argument.condition.CommandCondition.and;

public class RemoveCommand implements CommandModule<CommandSender> {
    private static final Logger LOGGER = NamespacedLoggerFactory.create(RemoveCommand.class);
    private static final Key<Waypoint> WAYPOINT_KEY = Key.named(Waypoint.class, "waypoint");
    private final CommandHelper helper;
    private final WaypointService waypointService;
    private final NavigationService navigationService;
    private final MessageConfig messageConfig;

    @Inject
    public RemoveCommand(
            final CommandHelper helper,
            final WaypointService waypointService,
            final NavigationService navigationService,
            final MessageConfig messageConfig
    ) {
        this.helper = helper;
        this.waypointService = waypointService;
        this.navigationService = navigationService;
        this.messageConfig = messageConfig;
    }

    @Override
    public CommandChain<CommandSender> chain(final CommandChainFactory<CommandSender> factory) {
        return factory.newChain()
                .then(factory.literal("waypoint").aliases("wp").build())
                .then(factory.literal("remove").aliases("rm").expect(and(
                        this.helper.perm("waypoint.remove"), this.helper.isPlayer()
                )).build())
                .arguments()
                .then(factory.required(WAYPOINT_KEY).mapWith(this.helper.ownedWaypoint()).build())
                .build();
    }

    @Override
    public void execute(final CommandContext<CommandSender> context) {
        final Waypoint waypoint = context.require(WAYPOINT_KEY);
        final CommandSender sender = context.source();

        this.waypointService.removeWaypoint(waypoint)
                .thenRun(() -> this.navigationService.cancelAll(waypoint))
                .thenApply(x -> this.messageConfig.get(MessageKeys.Waypoint.DELETION_SUCCESS)
                        .with(Placeholder.of("name", waypoint.getName()))
                        .make())
                .thenAccept(sender::sendMessage)
                .exceptionally(capture(sender, this.messageConfig.get(MessageKeys.Waypoint.DELETION_FAILURE).make(), "Failed to remove waypoint", LOGGER));
    }
}
