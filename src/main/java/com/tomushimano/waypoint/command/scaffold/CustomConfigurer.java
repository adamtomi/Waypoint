package com.tomushimano.waypoint.command.scaffold;

import com.tomushimano.waypoint.command.CommandManager;
import com.tomushimano.waypoint.command.scaffold.condition.IsPlayer;
import com.tomushimano.waypoint.command.scaffold.mapper.WaypointArgumentMapper;
import com.tomushimano.waypoint.command.scaffold.registration.CommandMapAccess;
import com.tomushimano.waypoint.core.Waypoint;
import grapefruit.command.dispatcher.config.DispatcherConfigurer;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;

import static com.tomushimano.waypoint.command.scaffold.WaypointContextKeys.SENDER_KEY;
import static com.tomushimano.waypoint.command.scaffold.mapper.WaypointArgumentMapper.OWNING_NAME;
import static java.util.Objects.requireNonNull;

public class CustomConfigurer extends DispatcherConfigurer {
    private final JavaPlugin plugin;
    private final CommandManager commandManager;
    private final WaypointArgumentMapper.Provider provider;

    public CustomConfigurer(JavaPlugin plugin, WaypointArgumentMapper.Provider provider, CommandManager commandManager) {
        this.plugin = requireNonNull(plugin, "plugin cannot be null");
        this.provider = requireNonNull(provider, "provider cannot be null");
        this.commandManager = requireNonNull(commandManager, "commandManager cannot be null");
    }

    @Override
    public void configure() {
        // Setup authorization
        authorize((perm, context) -> context.require(SENDER_KEY).hasPermission(perm));

        // Configure registration handler
        registrations().using(new CommandMapAccess(this.commandManager, this.plugin));

        // Register argument mappers
        map(Waypoint.class).using(this.provider.standard());
        map(Waypoint.class).namedAs(OWNING_NAME).using(this.provider.owning());

        // Register conditions
        conditions(new IsPlayer());
    }

    public static final class Factory {
        private final JavaPlugin plugin;
        private final WaypointArgumentMapper.Provider provider;

        @Inject
        public Factory(JavaPlugin plugin, WaypointArgumentMapper.Provider provider) {
            this.plugin = plugin;
            this.provider = provider;
        }

        public DispatcherConfigurer create(CommandManager commandManager) {
            return new CustomConfigurer(this.plugin, this.provider, commandManager);
        }
    }
}
