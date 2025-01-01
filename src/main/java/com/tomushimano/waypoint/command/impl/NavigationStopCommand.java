package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.command.scaffold.CommandHelper;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.core.navigation.NavigationService;
import com.tomushimano.waypoint.di.qualifier.Lang;
import com.tomushimano.waypoint.message.Messages;
import grapefruit.command.CommandModule;
import grapefruit.command.argument.CommandChain;
import grapefruit.command.argument.CommandChainFactory;
import grapefruit.command.dispatcher.CommandContext;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.Optional;

import static grapefruit.command.argument.condition.CommandCondition.and;

public class NavigationStopCommand implements CommandModule<CommandSender> {
    private final CommandHelper helper;
    private final NavigationService navigationService;
    private final Configurable config;

    @Inject
    public NavigationStopCommand(
            final CommandHelper helper,
            final NavigationService navigationService,
            final @Lang Configurable config
    ) {
        this.helper = helper;
        this.navigationService = navigationService;
        this.config = config;
    }

    @Override
    public CommandChain<CommandSender> chain(final CommandChainFactory<CommandSender> factory) {
        return factory.newChain()
                .then(factory.literal("waypoint").aliases("wp").build())
                .then(factory.literal("navigation").aliases("nav").expect(and(
                        this.helper.perm("waypoint.navigation"), this.helper.isPlayer()
                )).build())
                .then(factory.literal("stop").aliases("end").build())
                .build();
    }

    @Override
    public void execute(final CommandContext<CommandSender> context) {
        final Player sender = (Player) context.source();
        final Optional<Waypoint> destination = this.navigationService.currentDestination(sender);
        if (destination.isEmpty()) {
            Messages.NAVIGATION__STOP_NONE_RUNNING.from(this.config).print(sender);
            return;
        }

        this.navigationService.stopNavigation(sender);
        Messages.NAVIGATION__STOPPED.from(this.config, destination.orElseThrow()).print(sender);
    }
}
