package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.command.scaffold.CommandHelper;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.core.WaypointService;
import com.tomushimano.waypoint.di.qualifier.Lang;
import grapefruit.command.argument.CommandChain;
import grapefruit.command.argument.CommandChainFactory;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.util.key.Key;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

import static grapefruit.command.argument.condition.CommandCondition.and;

public class EditCommand extends UpdateWaypointCommand {
    private static final Key<Waypoint> WAYPOINT_KEY = Key.named(Waypoint.class, "waypoint");
    private static final Key<String> NAME_KEY = Key.named(String.class, "name");
    private static final Key<TextColor> COLOR_KEY = Key.named(TextColor.class, "color");
    private static final Key<Boolean> TOGGLE_VISIBILITY = Key.named(Boolean.class, "toggle-visibility");
    private final CommandHelper helper;

    @Inject
    public EditCommand(
            final WaypointService waypointService,
            final @Lang Configurable config,
            final CommandHelper helper
    ) {
        super(waypointService, config);
        this.helper = helper;
    }

    @Override
    public CommandChain<CommandSender> chain(final CommandChainFactory<CommandSender> factory) {
        return factory.newChain()
                .then(factory.literal("waypoint").aliases("wp").build())
                .then(factory.literal("edit").expect(and(
                        this.helper.perm("waypoint.edit"), this.helper.isPlayer()
                )).build())
                .arguments()
                .then(factory.required(WAYPOINT_KEY).mapWith(this.helper.ownedWaypoint()).build())
                .flags()
                .then(factory.valueFlag(NAME_KEY).mapWith(this.helper.name()).assumeShorthand().build())
                .then(factory.valueFlag(COLOR_KEY).mapWith(this.helper.textColor()).assumeShorthand().build())
                .then(factory.presenceFlag(TOGGLE_VISIBILITY).assumeShorthand().build())
                .build();
    }

    @Override
    public void execute(final CommandContext<CommandSender> context) {
        final Player sender = (Player) context.source();
        final Waypoint waypoint = context.require(WAYPOINT_KEY);
        final String name = context.nullable(NAME_KEY);
        final TextColor color = context.nullable(COLOR_KEY);
        final boolean toggleGlobality = context.has(TOGGLE_VISIBILITY);

        if (name != null) waypoint.setName(name);
        if (color != null) waypoint.setColor(color);
        if (toggleGlobality) waypoint.setPublic(!waypoint.isPublic());
        updateAndReport(sender, waypoint);
    }
}
