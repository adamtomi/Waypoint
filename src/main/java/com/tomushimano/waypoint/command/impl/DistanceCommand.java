package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.command.scaffold.mapper.WaypointArgumentMapper;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.di.qualifier.Accessible;
import com.tomushimano.waypoint.di.qualifier.Lang;
import com.tomushimano.waypoint.message.Messages;
import grapefruit.command.CommandModule;
import grapefruit.command.argument.CommandChain;
import grapefruit.command.argument.CommandChainFactory;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.util.key.Key;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

import static com.tomushimano.waypoint.command.scaffold.condition.InWorldCondition.inWorld;
import static com.tomushimano.waypoint.command.scaffold.condition.IsPlayerCondition.isPlayer;
import static com.tomushimano.waypoint.command.scaffold.condition.PermissionCondition.perm;
import static grapefruit.command.argument.condition.CommandCondition.and;

public class DistanceCommand implements CommandModule<CommandSender> {
    private static final Key<Waypoint> WAYPOINT_KEY = Key.named(Waypoint.class, "waypoint");
    private final Configurable config;
    private final WaypointArgumentMapper waypointMapper;

    @Inject
    public DistanceCommand(final @Lang Configurable config, final @Accessible WaypointArgumentMapper waypointMapper) {
        this.config = config;
        this.waypointMapper = waypointMapper;
    }

    @Override
    public CommandChain<CommandSender> chain(final CommandChainFactory<CommandSender> factory) {
        return factory.newChain()
                .then(factory.literal("waypoint").aliases("wp").build())
                .then(factory.literal("distance").aliases("dist").expect(and(perm("waypoint.distance"), isPlayer(), inWorld(WAYPOINT_KEY))).build())
                .arguments()
                .then(factory.required(WAYPOINT_KEY).mapWith(this.waypointMapper).build())
                .build();
    }

    @Override
    public void execute(final CommandContext<CommandSender> context) {
        final Player sender = (Player) context.source();
        final Waypoint waypoint = context.require(WAYPOINT_KEY);

        final long distance = waypoint.distance(sender);
        Messages.WAYPOINT__DISTANCE.from(this.config, distance).print(sender);
    }
}
