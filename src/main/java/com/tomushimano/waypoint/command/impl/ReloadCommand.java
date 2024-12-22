package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.command.scaffold.CommandHelper;
import com.tomushimano.waypoint.config.ConfigHelper;
import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import com.tomushimano.waypoint.core.WaypointService;
import grapefruit.command.CommandModule;
import grapefruit.command.argument.CommandChain;
import grapefruit.command.argument.CommandChainFactory;
import grapefruit.command.dispatcher.CommandContext;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;

public class ReloadCommand implements CommandModule<CommandSender> {
    private final CommandHelper commandHelper;
    private final ConfigHelper configHelper;
    private final MessageConfig messageConfig;
    private final WaypointService waypointService;

    @Inject
    public ReloadCommand(
            final CommandHelper commandHelper,
            final ConfigHelper configHelper,
            final MessageConfig messageConfig,
            final WaypointService waypointService
    ) {
        this.commandHelper = commandHelper;
        this.configHelper = configHelper;
        this.messageConfig = messageConfig;
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

        sender.sendMessage(this.messageConfig.get(MessageKeys.Admin.RELOAD_INITIATED).make());
        if (!this.configHelper.reloadAll()) {
            sender.sendMessage(this.messageConfig.get(MessageKeys.Admin.RELOAD_FAILURE).make());
        } else {
            this.waypointService.getLoadedWaypoints().forEach(this.waypointService::rerenderForTargets);
            long deltaT = System.currentTimeMillis() - start;
            sender.sendMessage(this.messageConfig.get(MessageKeys.Admin.RELOAD_SUCCESS)
                    .with(Placeholder.of("duration", deltaT))
                    .make());
        }
    }
}
