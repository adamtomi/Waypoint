package com.tomushimano.waypoint.command.scaffold;

import com.tomushimano.waypoint.command.CommandManager;
import com.tomushimano.waypoint.command.scaffold.registration.CommandMapAccess;
import grapefruit.command.dispatcher.config.DispatcherConfigurer;
import org.bukkit.plugin.java.JavaPlugin;

import static com.tomushimano.waypoint.command.scaffold.WaypointContextKeys.SENDER_KEY;
import static java.util.Objects.requireNonNull;

public class CustomConfigurer extends DispatcherConfigurer {
    private final JavaPlugin plugin;
    private final CommandManager commandManager;

    public CustomConfigurer(JavaPlugin plugin, CommandManager commandManager) {
        this.plugin = requireNonNull(plugin, "plugin cannot be null");
        this.commandManager = requireNonNull(commandManager, "commandManager cannot be null");
    }

    @Override
    public void configure() {
        // Setup authorization
        authorize((perm, context) -> context.require(SENDER_KEY).hasPermission(perm));

        // Configure registration handler
        registrations().using(new CommandMapAccess(this.commandManager, this.plugin));
    }
}
