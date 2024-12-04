package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.command.scaffold.mapper.NamedTextColorArgumentMapper;
import com.tomushimano.waypoint.command.scaffold.mapper.WaypointArgumentMapper;
import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.core.WaypointService;
import grapefruit.command.argument.CommandChain;
import grapefruit.command.argument.CommandChainFactory;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.util.key.Key;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

import static grapefruit.command.argument.mapper.builtin.StringArgumentMapper.word;

public class EditCommand extends UpdateWaypointCommand {
    private static final Key<Waypoint> WAYPOINT_KEY = Key.named(Waypoint.class, "waypoint");
    private static final Key<String> NAME_KEY = Key.named(String.class, "name");
    private static final Key<NamedTextColor> COLOR_KEY = Key.named(NamedTextColor.class, "color");
    private static final Key<Boolean> TOGGLE_GLOBALITY_KEY = Key.named(Boolean.class, "toggle-globality");
    private final WaypointArgumentMapper.Provider waypointArgumentMapperProvider;

    @Inject
    public EditCommand(
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
                .then(factory.literal("edit").require("waypoint.edit").build())
                .arguments()
                .then(factory.required(WAYPOINT_KEY).mapWith(this.waypointArgumentMapperProvider.owning()).build())
                .flags()
                .then(factory.valueFlag(NAME_KEY).mapWith(word()).assumeShorthand().build())
                .then(factory.valueFlag(COLOR_KEY).mapWith(new NamedTextColorArgumentMapper(null)).assumeShorthand().build())
                .then(factory.presenceFlag(TOGGLE_GLOBALITY_KEY).assumeShorthand().build())
                .build();
    }

    @Override
    public void execute(final CommandContext<CommandSender> context) {
        final Player sender = (Player) context.source();
        final Waypoint waypoint = context.require(WAYPOINT_KEY);
        final @Nullable String name = context.nullable(NAME_KEY);
        final @Nullable NamedTextColor color = context.nullable(COLOR_KEY);
        final boolean toggleGlobality = context.has(TOGGLE_GLOBALITY_KEY);

        if (name != null) waypoint.setName(name);
        if (color != null) waypoint.setColor(color);
        if (toggleGlobality) waypoint.setGlobal(!waypoint.isGlobal());
        updateAndReport(sender, waypoint);
    }
}
