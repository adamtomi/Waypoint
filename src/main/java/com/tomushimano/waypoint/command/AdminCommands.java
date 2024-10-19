package com.tomushimano.waypoint.command;

import com.tomushimano.waypoint.command.scaffold.CommandModule;
import com.tomushimano.waypoint.command.scaffold.Sender;
import com.tomushimano.waypoint.config.ConfigHelper;
import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.core.WaypointService;

import grapefruit.command.runtime.annotation.Arg;
import grapefruit.command.runtime.annotation.Command;
import grapefruit.command.runtime.dispatcher.CommandDispatcher;
import grapefruit.command.runtime.generated.CommandContainer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

import static com.tomushimano.waypoint.util.BukkitUtil.formatPosition;

public class AdminCommands implements CommandModule {
    private final CommandContainer container = new AdminCommands_Container(this);
    private final ConfigHelper configHelper;
    private final MessageConfig messageConfig;
    private final WaypointService waypointService;

    @Inject
    public AdminCommands(ConfigHelper configHelper, MessageConfig messageConfig, WaypointService waypointService) {
        this.configHelper = configHelper;
        this.messageConfig = messageConfig;
        this.waypointService = waypointService;
    }

    @Override
    public void registerCommands(CommandDispatcher dispatcher) {
        dispatcher.register(this.container);
    }

    @Command(route = "waypointadmin|wpa reload", permission = "waypoint.admin.reload")
    public void reload(@Sender CommandSender sender) {
        long start = System.currentTimeMillis();
        sender.sendMessage(this.messageConfig.get(MessageKeys.Admin.RELOAD_INITIATED).make());
        if (!this.configHelper.reloadAll()) {
            sender.sendMessage(this.messageConfig.get(MessageKeys.Admin.RELOAD_FAILURE).make());
        } else {
            long deltaT = System.currentTimeMillis() - start;
            this.waypointService.getLoadedWaypoints().forEach(this.waypointService::rerenderForTargets);
            sender.sendMessage(this.messageConfig.get(MessageKeys.Admin.RELOAD_SUCCESS)
                    .with(Placeholder.of("duration", deltaT))
                    .make());
        }
    }

    @Command(route = "waypointadmin|wpa info", permission = "waypoint.admin.info")
    public void info(@Sender Player sender, @Arg Waypoint waypoint) {
        sender.sendMessage(this.messageConfig.get(MessageKeys.Admin.INFO).with(
                Placeholder.of("name", waypoint.getName()),
                Placeholder.of("uniqueId", waypoint.getUniqueId()),
                Placeholder.of("global", waypoint.isGlobal()),
                Placeholder.of("owner", Bukkit.getOfflinePlayer(waypoint.getOwnerId()).getName()),
                Placeholder.of("coordinates", formatPosition(waypoint.getPosition())))
                .make());
    }
}
