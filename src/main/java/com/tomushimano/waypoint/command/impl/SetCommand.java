package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.command.scaffold.mapper.ArgumentMapperHolder;
import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import com.tomushimano.waypoint.core.WaypointService;
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

import static com.tomushimano.waypoint.util.BukkitUtil.formatPosition;
import static com.tomushimano.waypoint.util.ExceptionUtil.capture;

// TODO isPlayer condition
public class SetCommand implements CommandModule<CommandSender> {
    private static final Logger LOGGER = NamespacedLoggerFactory.create(SetCommand.class);
    private static final Key<String> NAME_KEY = Key.named(String.class, "name");
    private static final Key<Boolean> GLOBAL_KEY = Key.named(Boolean.class, "global");
    private static final Key<TextColor> COLOR_KEY = Key.named(TextColor.class, "color");
    private final ArgumentMapperHolder mapperHolder;
    private final WaypointService waypointService;
    private final MessageConfig messageConfig;

    @Inject
    public SetCommand(
            final ArgumentMapperHolder mapperHolder,
            final WaypointService waypointService,
            final MessageConfig messageConfig
    ) {
        this.mapperHolder = mapperHolder;
        this.waypointService = waypointService;
        this.messageConfig = messageConfig;
    }

    @Override
    public CommandChain<CommandSender> chain(final CommandChainFactory<CommandSender> factory) {
        return factory.newChain()
                .then(factory.literal("waypoint").aliases("wp").build())
                .then(factory.literal("set").require("waypoint.set").build())
                .arguments()
                .then(factory.required(NAME_KEY).mapWith(this.mapperHolder.varchar255()).build())
                .flags()
                .then(factory.presenceFlag(GLOBAL_KEY).assumeShorthand().build())
                .then(factory.valueFlag(COLOR_KEY).assumeShorthand().mapWith(this.mapperHolder.textColor()).build())
                .build();
    }

    @Override
    public void execute(final CommandContext<CommandSender> context) {
        final Player sender = (Player) context.source();
        final String name = context.require(NAME_KEY);

        // Check if a waypoint with this name exists already
        if (this.waypointService.getByName(sender, name).isPresent()) {
            sender.sendMessage(this.messageConfig.get(MessageKeys.Waypoint.CREATION_ALREADY_EXISTS)
                    .with(Placeholder.of("name", name))
                    .make());
            return;
        }

        final boolean global = context.has(GLOBAL_KEY);
        final TextColor color = context.getOrDefault(COLOR_KEY, NamedTextColor.WHITE);

        this.waypointService.createWaypoint(sender, name, color, global)
                .thenApply(x -> this.messageConfig.get(MessageKeys.Waypoint.CREATION_SUCCESS)
                        .with(
                                Placeholder.of("name", x.getName()),
                                Placeholder.of("world", x.getPosition().getWorldName()),
                                Placeholder.of("coordinates", formatPosition(x.getPosition()))
                        )
                        .make())
                .thenAccept(sender::sendMessage)
                .exceptionally(capture(sender, this.messageConfig.get(MessageKeys.Waypoint.CREATION_FAILURE).make(), "Failed to create waypoint", LOGGER));
    }
}
