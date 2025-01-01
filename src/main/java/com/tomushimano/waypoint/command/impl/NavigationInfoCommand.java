package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.command.scaffold.CommandHelper;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.core.navigation.NavigationService;
import com.tomushimano.waypoint.di.qualifier.Lang;
import com.tomushimano.waypoint.message.Message;
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

public class NavigationInfoCommand implements CommandModule<CommandSender> {
    private final CommandHelper helper;
    private final NavigationService navigationService;
    private final Configurable config;

    @Inject
    public NavigationInfoCommand(
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
                .then(factory.literal("info").aliases("i").build())
                .build();
    }

    @Override
    public void execute(final CommandContext<CommandSender> context) {
        final Player sender = (Player) context.source();
        final Optional<Waypoint> destination = this.navigationService.currentDestination(sender);
        final Message message = destination.map(x -> Messages.NAVIGATION__INFO.from(this.config, x))
                .orElse(Messages.NAVIGATION__INFO_NONE.from(this.config));
        message.print(sender);
    }
}
