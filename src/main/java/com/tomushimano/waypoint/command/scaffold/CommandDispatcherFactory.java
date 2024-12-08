package com.tomushimano.waypoint.command.scaffold;

import com.tomushimano.waypoint.command.CommandManager;
import grapefruit.command.dispatcher.CommandDispatcher;
import org.bukkit.command.CommandSender;

public interface CommandDispatcherFactory {

    CommandDispatcher<CommandSender> create(final CommandManager commandManager);
}
