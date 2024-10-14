package com.tomushimano.waypoint.command.scaffold.event;

import org.bukkit.command.CommandSender;

public abstract class CommandEvent {
    private final CommandSender sender;
    private final String command;

    public CommandEvent(CommandSender sender, String command) {
        this.sender = sender;
        this.command = command;
    }

    public CommandSender getSender() {
        return this.sender;
    }

    public String getCommand() {
        return this.command;
    }
}
