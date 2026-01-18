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
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;

import static com.tomushimano.waypoint.command.scaffold.condition.IsPlayerCondition.isPlayer;
import static com.tomushimano.waypoint.command.scaffold.condition.PermissionCondition.perm;
import static grapefruit.command.argument.condition.CommandCondition.and;

public class InfoCommand implements CommandModule<CommandSender> {
    private static final Key<Waypoint> WAYPOINT_KEY = Key.named(Waypoint.class, "waypoint");
    private final Configurable config;
    private final WaypointArgumentMapper waypointMapper;

    @Inject
    public InfoCommand(final @Lang Configurable config, final @Accessible WaypointArgumentMapper waypointMapper) {
        this.config = config;
        this.waypointMapper = waypointMapper;
    }

    @Override
    public CommandChain<CommandSender> chain(final CommandChainFactory<CommandSender> factory) {
        return factory.newChain()
                .then(factory.literal("waypoint").aliases("wp").build())
                .then(factory.literal("info").aliases("i").expect(and(perm("waypoint.info"), isPlayer())).build())
                .arguments()
                .then(factory.required(WAYPOINT_KEY).mapWith(this.waypointMapper).build())
                .build();
    }

    @Override
    public void execute(final CommandContext<CommandSender> context) {
        final Waypoint waypoint = context.require(WAYPOINT_KEY);
        final OfflinePlayer owner = Bukkit.getOfflinePlayer(waypoint.getOwnerId());
        Messages.WAYPOINT__INFO.from(this.config, waypoint, owner).print(context.source());
    }
}
