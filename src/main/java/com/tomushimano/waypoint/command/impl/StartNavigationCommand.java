package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.command.scaffold.CommandHelper;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.config.StandardKeys;
import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.core.navigation.NavigationService;
import com.tomushimano.waypoint.di.qualifier.Cfg;
import grapefruit.command.CommandModule;
import grapefruit.command.argument.CommandChain;
import grapefruit.command.argument.CommandChainFactory;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.util.key.Key;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

import static grapefruit.command.argument.condition.CommandCondition.and;

public class StartNavigationCommand implements CommandModule<CommandSender> {
    private static final Key<Waypoint> DESTINATION_KEY = Key.named(Waypoint.class, "destination");
    private static final Key<Boolean> FORCE_KEY = Key.named(Boolean.class, "force");
    private final CommandHelper helper;
    private final NavigationService navigationService;
    private final MessageConfig messageConfig;
    private final Configurable config;

    @Inject
    public StartNavigationCommand(
            final CommandHelper helper,
            final NavigationService navigationService,
            final MessageConfig messageConfig,
            final @Cfg Configurable config
    ) {
        this.helper = helper;
        this.navigationService = navigationService;
        this.messageConfig = messageConfig;
        this.config = config;
    }

    @Override
    public CommandChain<CommandSender> chain(final CommandChainFactory<CommandSender> factory) {
        return factory.newChain()
                .then(factory.literal("waypoint").aliases("wp").build())
                .then(factory.literal("navigation").aliases("nav").expect(and(
                        this.helper.perm("waypoint.navigation"), this.helper.isPlayer()
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

        if (this.navigationService.isNavigating(sender)) {
            if (context.has(FORCE_KEY)) {
                final Waypoint currentDestination = this.navigationService.currentDestination(sender).orElseThrow();
                sender.sendMessage(this.messageConfig.get(MessageKeys.Navigation.START_RUNNING_CANCELLED)
                        .with(Placeholder.of("name", currentDestination.getName()))
                        .make());

                this.navigationService.stopNavigation(sender);
            } else {
                sender.sendMessage(this.messageConfig.get(MessageKeys.Navigation.START_ALREADY_RUNNING).make());
                return;
            }
        }

        final Waypoint waypoint = context.require(DESTINATION_KEY);
        final int minDistance = this.config.get(StandardKeys.Navigation.MIN_REQUIRED_DISTANCE);

        if (waypoint.distance(sender) < minDistance) {
            sender.sendMessage(this.messageConfig.get(MessageKeys.Navigation.START_TOO_CLOSE)
                    .with(Placeholder.of("blocks", minDistance))
                    .make());
            return;
        }

        final String worldName = waypoint.getPosition().getWorldName();
        if (!worldName.equals(sender.getWorld().getName())) {
            sender.sendMessage(this.messageConfig.get(MessageKeys.Navigation.START_WORLD_ERROR)
                    .with(Placeholder.of("name", worldName))
                    .make());
            return;
        }

        sender.sendMessage(this.messageConfig.get(MessageKeys.Navigation.STARTED)
                .with(Placeholder.of("name", waypoint.getName()))
                .make());
        this.navigationService.startNavigation(sender, waypoint, () -> sender.sendMessage(this.messageConfig.get(MessageKeys.Navigation.ARRIVED)
                .with(Placeholder.of("name", waypoint.getName()))
                .make()));
    }
}
