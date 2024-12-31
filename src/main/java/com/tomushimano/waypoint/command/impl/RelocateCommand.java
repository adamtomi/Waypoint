package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.command.scaffold.CommandHelper;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.core.WaypointService;
import com.tomushimano.waypoint.core.navigation.NavigationService;
import com.tomushimano.waypoint.di.qualifier.Lang;
import com.tomushimano.waypoint.util.Position;
import grapefruit.command.argument.CommandChain;
import grapefruit.command.argument.CommandChainFactory;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.util.key.Key;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

import static grapefruit.command.argument.condition.CommandCondition.and;

public class RelocateCommand extends UpdateWaypointCommand {
    private static final Key<Waypoint> WAYPOINT_KEY = Key.named(Waypoint.class, "waypoint");
    private final CommandHelper helper;
    private final NavigationService navigationService;

    @Inject
    public RelocateCommand(
            final WaypointService waypointService,
            final @Lang Configurable config,
            final CommandHelper helper,
            final NavigationService navigationService
    ) {
        super(waypointService, config);
        this.helper = helper;
        this.navigationService = navigationService;
    }

    @Override
    public CommandChain<CommandSender> chain(final CommandChainFactory<CommandSender> factory) {
        return factory.newChain()
                .then(factory.literal("waypoint").aliases("wp").build())
                .then(factory.literal("relocate").aliases("reloc", "movehere").expect(and(
                        this.helper.perm("waypoint.relocate"), this.helper.isPlayer()
                )).build())
                .arguments()
                .then(factory.required(WAYPOINT_KEY).mapWith(this.helper.ownedWaypoint()).build())
                .build();
    }

    @Override
    public void execute(final CommandContext<CommandSender> context) {
        final Player sender = (Player) context.source();
        final Waypoint waypoint = context.require(WAYPOINT_KEY);
        waypoint.setPosition(Position.from(sender.getLocation()));
        this.navigationService.updateNavigations(waypoint);

        updateAndReport(sender, waypoint);
    }
}
