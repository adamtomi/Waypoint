package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.command.scaffold.CommandHelper;
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

import static com.tomushimano.waypoint.util.ExceptionUtil.capture;
import static grapefruit.command.argument.condition.CommandCondition.and;

public class SetCommand implements CommandModule<CommandSender> {
    private static final Logger LOGGER = NamespacedLoggerFactory.create(SetCommand.class);
    private static final Key<String> NAME_KEY = Key.named(String.class, "name");
    private static final Key<Boolean> GLOBAL_KEY = Key.named(Boolean.class, "global");
    private static final Key<TextColor> COLOR_KEY = Key.named(TextColor.class, "color");
    private final CommandHelper helper;
    private final WaypointService waypointService;
    private final Configurable config;

    @Inject
    public SetCommand(
            final CommandHelper helper,
            final WaypointService waypointService,
            final @Lang Configurable config
    ) {
        this.helper = helper;
        this.waypointService = waypointService;
        this.config = config;
    }

    @Override
    public CommandChain<CommandSender> chain(final CommandChainFactory<CommandSender> factory) {
        return factory.newChain()
                .then(factory.literal("waypoint").aliases("wp").build())
                .then(factory.literal("set").expect(and(
                        this.helper.perm("waypoint.set"), this.helper.isPlayer()
                )).build())
                .arguments()
                .then(factory.required(NAME_KEY).mapWith(this.helper.name()).build())
                .flags()
                .then(factory.presenceFlag(GLOBAL_KEY).assumeShorthand().build())
                .then(factory.valueFlag(COLOR_KEY).assumeShorthand().mapWith(this.helper.textColor()).build())
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

        final boolean global = context.has(GLOBAL_KEY);
        final TextColor color = context.getOrDefault(COLOR_KEY, NamedTextColor.WHITE);

        this.waypointService.createWaypoint(sender, name, color, global)
                .thenAccept(x -> Messages.WAYPOINT__CREATION_SUCCESS.from(this.config, x).print(sender))
                .exceptionally(capture(() -> Messages.WAYPOINT__CREATION_FAILURE.from(this.config).print(sender), "Failed to create waypoint", LOGGER));
    }
}
