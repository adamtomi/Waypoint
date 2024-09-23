package com.tomushimano.waypoint.command;

import com.tomushimano.waypoint.command.scaffold.CommandHolder;
import com.tomushimano.waypoint.command.scaffold.Sender;
import com.tomushimano.waypoint.config.ConfigHelper;
import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import grapefruit.command.CommandContainer;
import grapefruit.command.annotation.CommandDefinition;
import grapefruit.command.dispatcher.CommandDispatcher;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;

public class AdminCommands implements CommandHolder {
    private final CommandContainer container = new AdminCommands_Container(this);
    private final ConfigHelper configHelper;
    private final MessageConfig messageConfig;

    @Inject
    public AdminCommands(ConfigHelper configHelper, MessageConfig messageConfig) {
        this.configHelper = configHelper;
        this.messageConfig = messageConfig;
    }

    @Override
    public void registerCommands(CommandDispatcher dispatcher) {
        dispatcher.register(this.container);
    }

    @CommandDefinition(route = "waypointadmin|wpa reload", permission = "waypoint.admin.reload")
    public void reload(@Sender CommandSender sender) {
        long start = System.currentTimeMillis();
        sender.sendMessage(this.messageConfig.get(MessageKeys.Admin.RELOAD_INITIATED).make());
        if (!this.configHelper.reloadAll()) {
            sender.sendMessage(this.messageConfig.get(MessageKeys.Admin.RELOAD_FAILURE).make());
        } else {
            long deltaT = System.currentTimeMillis() - start;
            sender.sendMessage(this.messageConfig.get(MessageKeys.Admin.RELOAD_SUCCESS)
                    .with(Placeholder.of("duration", deltaT))
                    .make());
        }
    }
}
