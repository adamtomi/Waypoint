package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.command.scaffold.mapper.WaypointArgumentMapper;
import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import com.tomushimano.waypoint.core.Waypoint;
import grapefruit.command.CommandModule;
import grapefruit.command.argument.CommandChain;
import grapefruit.command.argument.CommandChainFactory;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.util.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;

import static com.tomushimano.waypoint.util.BukkitUtil.formatPosition;

public class InfoCommand implements CommandModule<CommandSender> {
    private static final Key<Waypoint> WAYPOINT_KEY = Key.named(Waypoint.class, "waypoint");
    private final WaypointArgumentMapper.Provider waypointArgumentMapperProvider;
    private final MessageConfig messageConfig;

    @Inject
    public InfoCommand(
            final WaypointArgumentMapper.Provider waypointArgumentMapperProvider,
            final MessageConfig messageConfig
    ) {
        this.waypointArgumentMapperProvider = waypointArgumentMapperProvider;
        this.messageConfig = messageConfig;
    }

    @Override
    public CommandChain<CommandSender> chain(final CommandChainFactory<CommandSender> factory) {
        return factory.newChain()
                .then(factory.literal("waypoint").aliases("wp").build())
                .then(factory.literal("info").aliases("i").require("waypoint.info").build())
                .arguments()
                .then(factory.required(WAYPOINT_KEY).mapWith(this.waypointArgumentMapperProvider.standard()).build())
                .build();
    }

    @Override
    public void execute(final CommandContext<CommandSender> context) {
        final Waypoint waypoint = context.require(WAYPOINT_KEY);
        context.source().sendMessage(this.messageConfig.get(MessageKeys.Admin.INFO).with(
                        Placeholder.of("name", waypoint.getName()),
                        Placeholder.of("uniqueId", waypoint.getUniqueId()),
                        Placeholder.of("global", waypoint.isGlobal()),
                        Placeholder.of("owner", Bukkit.getOfflinePlayer(waypoint.getOwnerId()).getName()),
                        Placeholder.of("coordinates", formatPosition(waypoint.getPosition())))
                .make());
    }
}
