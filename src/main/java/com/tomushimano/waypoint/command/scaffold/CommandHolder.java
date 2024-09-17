package com.tomushimano.waypoint.command.scaffold;

import grapefruit.command.dispatcher.CommandDispatcher;

public interface CommandHolder {

    void registerCommands(CommandDispatcher dispatcher);
}
