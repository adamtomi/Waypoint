package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.command.scaffold.mapper.WaypointArgumentMapper;
import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.core.WaypointService;
import com.tomushimano.waypoint.util.Position;
import grapefruit.command.argument.CommandChain;
import grapefruit.command.argument.CommandChainFactory;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.util.key.Key;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class RelocateCommand extends UpdateWaypointCommand {
    private static final Key<Waypoint> WAYPOINT_KEY = Key.named(Waypoint.class, "waypoint");
    private final WaypointArgumentMapper.Provider waypointArgumentMapperProvider;

    @Inject
    public RelocateCommand(
            final WaypointService waypointService,
            final MessageConfig messageConfig,
            final WaypointArgumentMapper.Provider waypointArgumentMapperProvider
    ) {
        super(waypointService, messageConfig);
        this.waypointArgumentMapperProvider = waypointArgumentMapperProvider;
    }

    @Override
    public CommandChain<CommandSender> chain(final CommandChainFactory<CommandSender> factory) {
        return factory.newChain()
                .then(factory.literal("waypoint").aliases("wp").build())
                .then(factory.literal("relocate").aliases("reloc", "movehere").require("waypoint.relocate").build())
                .arguments()
                .then(factory.required(WAYPOINT_KEY).mapWith(this.waypointArgumentMapperProvider.owning()).build())
                .build();
    }

    @Override
    public void execute(final CommandContext<CommandSender> context) {
        final Player sender = (Player) context.source();
        final Waypoint waypoint = context.require(WAYPOINT_KEY);
        waypoint.setPosition(Position.from(sender.getLocation()));

        updateAndReport(sender, waypoint);
    }
}
