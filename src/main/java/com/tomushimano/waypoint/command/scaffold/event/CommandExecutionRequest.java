package com.tomushimano.waypoint.command.scaffold.event;

import org.bukkit.command.CommandSender;

public final class CommandExecutionRequest extends CommandEvent {

    public CommandExecutionRequest(CommandSender sender, String command) {
        super(sender, command);
    }
}
