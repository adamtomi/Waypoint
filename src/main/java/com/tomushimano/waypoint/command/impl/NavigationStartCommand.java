package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.command.scaffold.mapper.WaypointArgumentMapper;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.config.StandardKeys;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.core.navigation.NavigationService;
import com.tomushimano.waypoint.di.qualifier.Accessible;
import com.tomushimano.waypoint.di.qualifier.Cfg;
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
import java.util.Optional;

import static com.tomushimano.waypoint.command.scaffold.condition.InWorldCondition.inWorld;
import static com.tomushimano.waypoint.command.scaffold.condition.IsPlayerCondition.isPlayer;
import static com.tomushimano.waypoint.command.scaffold.condition.PermissionCondition.perm;
import static grapefruit.command.argument.condition.CommandCondition.and;

public class NavigationStartCommand implements CommandModule<CommandSender> {
    private static final Key<Waypoint> DESTINATION_KEY = Key.named(Waypoint.class, "destination");
    private static final Key<Boolean> FORCE_KEY = Key.named(Boolean.class, "force");
    private final NavigationService navigationService;
    private final WaypointArgumentMapper waypointMapper;
    private final Configurable langConfig;
    private final Configurable config;

    @Inject
    public NavigationStartCommand(
            final NavigationService navigationService,
            final @Accessible WaypointArgumentMapper waypointMapper,
            final @Lang Configurable langConfig,
            final @Cfg Configurable config
    ) {
        this.navigationService = navigationService;
        this.waypointMapper = waypointMapper;
        this.langConfig = langConfig;
        this.config = config;
    }

    @Override
    public CommandChain<CommandSender> chain(final CommandChainFactory<CommandSender> factory) {
        return factory.newChain()
                .then(factory.literal("waypoint").aliases("wp").build())
                .then(factory.literal("navigation").aliases("nav").expect(and(perm("waypoint.navigation"), isPlayer(), inWorld(DESTINATION_KEY))).build())
                .then(factory.literal("start").aliases("begin").build())
                .arguments()
                .then(factory.required(DESTINATION_KEY).mapWith(this.waypointMapper).build())
                .flags()
                .then(factory.boolFlag(FORCE_KEY).assumeShorthand().build())
                .build();
    }

    @Override
    public void execute(final CommandContext<CommandSender> context) {
        final Player sender = (Player) context.source();
        final Optional<Waypoint> current = this.navigationService.currentDestination(sender);
        if (current.isPresent()) {
            if (context.has(FORCE_KEY)) {
                Messages.NAVIGATION__START_RUNNING_CANCELLED.from(this.langConfig, current.orElseThrow()).print(sender);
                this.navigationService.stopNavigation(sender);
            } else {
                Messages.NAVIGATION__START_ALREAEDY_RUNNING.from(this.langConfig).print(sender);
                return;
            }
        }

        final Waypoint waypoint = context.require(DESTINATION_KEY);
        final int minimumDistance = this.config.get(StandardKeys.Navigation.MIN_REQUIRED_DISTANCE);

        if (waypoint.distance(sender) < minimumDistance) {
            Messages.NAVIGATION__START_TOO_CLOSE.from(this.langConfig, minimumDistance).print(sender);
            return;
        }

        Messages.NAVIGATION__STARTED.from(this.langConfig, waypoint).print(sender);
        this.navigationService.startNavigation(sender, waypoint, x -> Messages.NAVIGATION__ARRIVED.from(this.langConfig, x).print(sender));
    }
}
