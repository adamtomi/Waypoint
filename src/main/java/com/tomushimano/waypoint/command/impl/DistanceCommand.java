package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.command.scaffold.CommandHelper;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.core.Waypoint;
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

import static grapefruit.command.argument.condition.CommandCondition.and;

public class DistanceCommand implements CommandModule<CommandSender> {
    private static final Key<Waypoint> WAYPOINT_KEY = Key.named(Waypoint.class, "waypoint");
    private final CommandHelper helper;
    private final Configurable config;

    @Inject
    public DistanceCommand(final CommandHelper helper, final @Lang Configurable config) {
        this.helper = helper;
        this.config = config;
    }

    @Override
    public CommandChain<CommandSender> chain(final CommandChainFactory<CommandSender> factory) {
        return factory.newChain()
                .then(factory.literal("waypoint").aliases("wp").build())
                .then(factory.literal("distance").aliases("dist").expect(and(
                        this.helper.perm("waypoint.distance"),
                        this.helper.isPlayer(),
                        this.helper.inWorld(WAYPOINT_KEY)
                )).build())
                .arguments()
                .then(factory.required(WAYPOINT_KEY).mapWith(this.helper.stdWaypoint()).build())
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
