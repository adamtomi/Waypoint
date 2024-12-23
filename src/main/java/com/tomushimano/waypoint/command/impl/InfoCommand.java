package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.command.scaffold.CommandHelper;
import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import com.tomushimano.waypoint.core.Waypoint;
import grapefruit.command.CommandModule;
import grapefruit.command.argument.CommandChain;
import grapefruit.command.argument.CommandChainFactory;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.util.key.Key;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;

import static com.tomushimano.waypoint.util.BukkitUtil.formatPosition;
import static grapefruit.command.argument.condition.CommandCondition.and;

public class InfoCommand implements CommandModule<CommandSender> {
    private static final Key<Waypoint> WAYPOINT_KEY = Key.named(Waypoint.class, "waypoint");
    private final CommandHelper helper;
    private final MessageConfig messageConfig;

    @Inject
    public InfoCommand(
            final CommandHelper helper,
            final MessageConfig messageConfig
    ) {
        this.helper = helper;
        this.messageConfig = messageConfig;
    }

    @Override
    public CommandChain<CommandSender> chain(final CommandChainFactory<CommandSender> factory) {
        return factory.newChain()
                .then(factory.literal("waypoint").aliases("wp").build())
                .then(factory.literal("info").aliases("i").expect(and(
                        this.helper.perm("waypoint.info"), this.helper.isPlayer()
                )).build())
                .arguments()
                .then(factory.required(WAYPOINT_KEY).mapWith(this.helper.stdWaypoint()).build())
                .build();
    }

    @Override
    public void execute(final CommandContext<CommandSender> context) {
        final Waypoint waypoint = context.require(WAYPOINT_KEY);
        final String colorName = waypoint.getColor() instanceof NamedTextColor namedColor
                ? namedColor.toString()
                : "";

        context.source().sendMessage(this.messageConfig.get(MessageKeys.Admin.INFO).with(
                        Placeholder.of("name", waypoint.getName()),
                        Placeholder.of("uniqueId", waypoint.getUniqueId()),
                        Placeholder.of("global", waypoint.isGlobal()),
                        Placeholder.of("owner", Bukkit.getOfflinePlayer(waypoint.getOwnerId()).getName()),
                        Placeholder.of("coordinates", formatPosition(waypoint.getPosition())),
                        Placeholder.of("color", waypoint.getColor().asHexString()),
                        Placeholder.of("colorname", colorName))
                .make());
    }
}
