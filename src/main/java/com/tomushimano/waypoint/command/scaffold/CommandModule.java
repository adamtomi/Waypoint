package com.tomushimano.waypoint.command.scaffold;

import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import grapefruit.command.runtime.dispatcher.CommandDispatcher;
import org.slf4j.Logger;

public interface CommandModule {
    Logger LOGGER = NamespacedLoggerFactory.create(CommandModule.class);

    void registerCommands(CommandDispatcher dispatcher);
}
