package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.command.scaffold.CommandHelper;
import com.tomushimano.waypoint.config.ConfigHelper;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.core.WaypointService;
import com.tomushimano.waypoint.di.qualifier.Lang;
import com.tomushimano.waypoint.message.Messages;
import grapefruit.command.CommandModule;
import grapefruit.command.argument.CommandChain;
import grapefruit.command.argument.CommandChainFactory;
import grapefruit.command.dispatcher.CommandContext;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;

public class ReloadCommand implements CommandModule<CommandSender> {
    private final CommandHelper commandHelper;
    private final ConfigHelper configHelper;
    private final Configurable config;
    private final WaypointService waypointService;

    @Inject
    public ReloadCommand(
            final CommandHelper commandHelper,
            final ConfigHelper configHelper,
            final @Lang Configurable config,
            final WaypointService waypointService
    ) {
        this.commandHelper = commandHelper;
        this.configHelper = configHelper;
        this.config = config;
        this.waypointService = waypointService;
    }

    @Override
    public CommandChain<CommandSender> chain(final CommandChainFactory<CommandSender> factory) {
        return factory.newChain()
                .then(factory.literal("waypointadmin").aliases("wpa").build())
                .then(factory.literal("reload").aliases("rl").expect(this.commandHelper.perm("waypoint.admin.reload")).build())
                .build();
    }

    @Override
    public void execute(final CommandContext<CommandSender> context) {
        final long start = System.currentTimeMillis();
        final CommandSender sender = context.source();

        Messages.ADMIN__RELOAD_INITIATED.from(this.config).print(sender);
        if (!this.configHelper.reloadAll()) {
            Messages.ADMIN__RELOAD_FAILURE.from(this.config).print(sender);
        } else {
            this.waypointService.getLoadedWaypoints().forEach(this.waypointService::rerenderForTargets);
            long deltaT = System.currentTimeMillis() - start;
            Messages.ADMIN__RELOAD_SUCCESS.with(this.config, deltaT).print(sender);
        }
    }
}
