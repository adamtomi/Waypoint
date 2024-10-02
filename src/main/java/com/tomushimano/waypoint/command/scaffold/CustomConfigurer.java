package com.tomushimano.waypoint.command.scaffold;

import com.tomushimano.waypoint.command.CommandManager;
import com.tomushimano.waypoint.command.scaffold.mapper.NamedTextColorArgumentMapper;
import com.tomushimano.waypoint.command.scaffold.mapper.WaypointArgumentMapper;
import com.tomushimano.waypoint.command.scaffold.registration.CommandMapAccess;
import com.tomushimano.waypoint.core.Waypoint;
import grapefruit.command.argument.modifier.ArgumentModifier;
import grapefruit.command.dispatcher.condition.CommandCondition;
import grapefruit.command.dispatcher.config.DispatcherConfigurer;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import java.util.Set;

import static com.tomushimano.waypoint.command.scaffold.WaypointContextKeys.SENDER_KEY;
import static com.tomushimano.waypoint.command.scaffold.mapper.WaypointArgumentMapper.OWNING_NAME;
import static java.util.Objects.requireNonNull;

public class CustomConfigurer extends DispatcherConfigurer {
    private final JavaPlugin plugin;
    private final CommandManager commandManager;
    private final NamedTextColorArgumentMapper colorMapper;
    private final WaypointArgumentMapper.Provider provider;
    private final Set<CommandCondition> conditions;
    private final Set<ArgumentModifier.Factory<?>> factories;

    public CustomConfigurer(
            JavaPlugin plugin,
            NamedTextColorArgumentMapper colorMapper,
            WaypointArgumentMapper.Provider provider,
            Set<CommandCondition> conditions,
            Set<ArgumentModifier.Factory<?>> factories,
            CommandManager commandManager
    ) {
        this.plugin = requireNonNull(plugin, "plugin cannot be null");
        this.colorMapper = requireNonNull(colorMapper, "colorMapper cannot be null");
        this.provider = requireNonNull(provider, "provider cannot be null");
        this.conditions = requireNonNull(conditions, "conditions cannot be null");
        this.factories = requireNonNull(factories, "factories cannot be null");
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
        map(NamedTextColor.class).using(this.colorMapper);

        // Register conditions
        conditions(this.conditions);

        // Register modifier factories
        modifierFactories(this.factories);
    }

    public static final class Factory {
        private final JavaPlugin plugin;
        private final NamedTextColorArgumentMapper colorMapper;
        private final WaypointArgumentMapper.Provider provider;
        private final Set<CommandCondition> conditions;
        private final Set<ArgumentModifier.Factory<?>> factories;

        @Inject
        public Factory(
                JavaPlugin plugin,
                NamedTextColorArgumentMapper colorMapper,
                WaypointArgumentMapper.Provider provider,
                Set<CommandCondition> conditions,
                Set<ArgumentModifier.Factory<?>> factories
        ) {
            this.plugin = plugin;
            this.colorMapper = colorMapper;
            this.provider = provider;
            this.conditions = conditions;
            this.factories = factories;
        }

        public DispatcherConfigurer create(CommandManager commandManager) {
            return new CustomConfigurer(this.plugin, this.colorMapper, this.provider, this.conditions, this.factories, commandManager);
        }
    }
}
