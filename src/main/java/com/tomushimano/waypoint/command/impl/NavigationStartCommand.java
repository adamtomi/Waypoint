package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.command.scaffold.CommandHelper;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.config.StandardKeys;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.core.navigation.NavigationService;
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

import static grapefruit.command.argument.condition.CommandCondition.and;

public class NavigationStartCommand implements CommandModule<CommandSender> {
    private static final Key<Waypoint> DESTINATION_KEY = Key.named(Waypoint.class, "destination");
    private static final Key<Boolean> FORCE_KEY = Key.named(Boolean.class, "force");
    private final CommandHelper helper;
    private final NavigationService navigationService;
    private final Configurable langConfig;
    private final Configurable config;

    @Inject
    public NavigationStartCommand(
            final CommandHelper helper,
            final NavigationService navigationService,
            final @Lang Configurable langConfig,
            final @Cfg Configurable config
    ) {
        this.helper = helper;
        this.navigationService = navigationService;
        this.langConfig = langConfig;
        this.config = config;
    }

    @Override
    public CommandChain<CommandSender> chain(final CommandChainFactory<CommandSender> factory) {
        return factory.newChain()
                .then(factory.literal("waypoint").aliases("wp").build())
                .then(factory.literal("navigation").aliases("nav").expect(and(
                        this.helper.perm("waypoint.navigation"),
                        this.helper.isPlayer(),
                        this.helper.inWorld(DESTINATION_KEY)
                )).build())
                .then(factory.literal("start").aliases("begin").build())
                .arguments()
                .then(factory.required(DESTINATION_KEY).mapWith(this.helper.stdWaypoint()).build())
                .flags()
                .then(factory.presenceFlag(FORCE_KEY).assumeShorthand().build())
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
        this.navigationService.startNavigation(sender, waypoint, () -> Messages.NAVIGATION__ARRIVED.from(this.langConfig, waypoint).print(sender));
    }
}
