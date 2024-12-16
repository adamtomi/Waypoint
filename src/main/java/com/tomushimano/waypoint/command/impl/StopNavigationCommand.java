package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.command.scaffold.CommandHelper;
import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.core.navigation.NavigationService;
import grapefruit.command.CommandModule;
import grapefruit.command.argument.CommandChain;
import grapefruit.command.argument.CommandChainFactory;
import grapefruit.command.dispatcher.CommandContext;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

import java.util.Optional;

import static grapefruit.command.argument.condition.CommandCondition.and;

public class StopNavigationCommand implements CommandModule<CommandSender> {
    private final CommandHelper helper;
    private final NavigationService navigationService;
    private final MessageConfig messageConfig;

    @Inject
    public StopNavigationCommand(
            final CommandHelper helper,
            final NavigationService navigationService,
            final MessageConfig messageConfig
    ) {
        this.helper = helper;
        this.navigationService = navigationService;
        this.messageConfig = messageConfig;
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
            sender.sendMessage(this.messageConfig.get(MessageKeys.Navigation.STOP_NONE_RUNNING).make());
            return;
        }

        this.navigationService.stopNavigation(sender);
        final String name = destination.map(Waypoint::getName).orElseThrow();
        sender.sendMessage(this.messageConfig.get(MessageKeys.Navigation.STOPPED)
                .with(Placeholder.of("name", name))
                .make());
    }
}
