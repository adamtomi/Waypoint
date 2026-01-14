package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.core.WaypointService;
import com.tomushimano.waypoint.di.qualifier.Lang;
import com.tomushimano.waypoint.message.Messages;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import grapefruit.command.CommandModule;
import grapefruit.command.argument.CommandChain;
import grapefruit.command.argument.CommandChainFactory;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.util.key.Key;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.slf4j.Logger;

import javax.inject.Inject;

import static com.tomushimano.waypoint.command.scaffold.condition.IsPlayerCondition.isPlayer;
import static com.tomushimano.waypoint.command.scaffold.condition.PermissionCondition.perm;
import static com.tomushimano.waypoint.command.scaffold.mapper.NameArgumentMapper.name;
import static com.tomushimano.waypoint.command.scaffold.mapper.TextColorArgumentMapper.textColor;
import static com.tomushimano.waypoint.util.ExceptionUtil.capture;
import static grapefruit.command.argument.condition.CommandCondition.and;

public class SetCommand implements CommandModule<CommandSender> {
    private static final Logger LOGGER = NamespacedLoggerFactory.create(SetCommand.class);
    private static final Key<String> NAME_KEY = Key.named(String.class, "name");
    private static final Key<Boolean> PUBLIC_KEY = Key.named(Boolean.class, "public");
    private static final Key<TextColor> COLOR_KEY = Key.named(TextColor.class, "color");
    private final WaypointService waypointService;
    private final Configurable config;

    @Inject
    public SetCommand(final WaypointService waypointService, final @Lang Configurable config) {
        this.waypointService = waypointService;
        this.config = config;
    }

    @Override
    public CommandChain<CommandSender> chain(final CommandChainFactory<CommandSender> factory) {
        return factory.newChain()
                .then(factory.literal("waypoint").aliases("wp").build())
                .then(factory.literal("set").expect(and(perm("waypoint.set"), isPlayer())).build())
                .arguments()
                .then(factory.required(NAME_KEY).mapWith(name()).build())
                .flags()
                .then(factory.boolFlag(PUBLIC_KEY).assumeShorthand().build())
                .then(factory.valueFlag(COLOR_KEY).assumeShorthand().mapWith(textColor()).build())
                .build();
    }

    @Override
    public void execute(final CommandContext<CommandSender> context) {
        final Player sender = (Player) context.source();
        final String name = context.require(NAME_KEY);

        // Check if a waypoint with this name exists already
        if (this.waypointService.getByName(sender, name).isPresent()) {
            Messages.WAYPOINT__CREATION_ALREADY_EXISTS.from(this.config, name).print(sender);
            return;
        }

        final boolean isPublic = context.has(PUBLIC_KEY);
        final TextColor color = context.getOrDefault(COLOR_KEY, NamedTextColor.WHITE);

        this.waypointService.createWaypoint(sender, name, color, isPublic)
                .thenAccept(x -> Messages.WAYPOINT__CREATION_SUCCESS.from(this.config, x).print(sender))
                .exceptionally(capture(() -> Messages.WAYPOINT__CREATION_FAILURE.from(this.config).print(sender), "Failed to create waypoint", LOGGER));
    }
}
