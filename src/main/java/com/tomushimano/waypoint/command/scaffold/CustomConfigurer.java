package com.tomushimano.waypoint.command.scaffold;

import com.tomushimano.waypoint.command.scaffold.mapper.NamedTextColorArgumentMapper;
import com.tomushimano.waypoint.command.scaffold.mapper.WaypointArgumentMapper;
import com.tomushimano.waypoint.command.scaffold.bukkit.CommandMapAccess;
import com.tomushimano.waypoint.core.Waypoint;
import grapefruit.command.argument.modifier.ArgumentModifier;
import grapefruit.command.dispatcher.condition.CommandCondition;
import grapefruit.command.dispatcher.config.DispatcherConfigurer;
import net.kyori.adventure.text.format.NamedTextColor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Set;

import static com.tomushimano.waypoint.command.scaffold.WaypointContextKeys.SENDER_KEY;
import static com.tomushimano.waypoint.command.scaffold.mapper.WaypointArgumentMapper.OWNING_NAME;

@Singleton
public class CustomConfigurer extends DispatcherConfigurer {
    private final NamedTextColorArgumentMapper colorMapper;
    private final WaypointArgumentMapper.Provider provider;
    private final Set<CommandCondition> conditions;
    private final Set<ArgumentModifier.Factory<?>> factories;
    private final CommandMapAccess commandMapAccess;

    @Inject
    public CustomConfigurer(
            NamedTextColorArgumentMapper colorMapper,
            WaypointArgumentMapper.Provider provider,
            Set<CommandCondition> conditions,
            Set<ArgumentModifier.Factory<?>> factories,
            CommandMapAccess commandMapAccess
    ) {
        this.colorMapper = colorMapper;
        this.provider = provider;
        this.conditions = conditions;
        this.factories = factories;
        this.commandMapAccess = commandMapAccess;
    }

    @Override
    public void configure() {
        // Setup authorization
        authorize((perm, context) -> context.require(SENDER_KEY).hasPermission(perm));

        // Configure registration handler
        registrations().using(this.commandMapAccess);

        // Register argument mappers
        map(Waypoint.class).using(this.provider.standard());
        map(Waypoint.class).namedAs(OWNING_NAME).using(this.provider.owning());
        map(NamedTextColor.class).using(this.colorMapper);

        // Register conditions
        conditions(this.conditions);

        // Register modifier factories
        modifierFactories(this.factories);
    }
}
