package com.tomushimano.waypoint.di.module;

import com.tomushimano.waypoint.command.AdminCommands;
import com.tomushimano.waypoint.command.WaypointCommands;
import com.tomushimano.waypoint.command.scaffold.CommandModule;
import com.tomushimano.waypoint.command.scaffold.condition.IsPlayer;
import com.tomushimano.waypoint.command.scaffold.modifier.MaxModifier;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;
import grapefruit.command.argument.modifier.ArgumentModifier;
import grapefruit.command.dispatcher.condition.CommandCondition;

@Module
public interface CommandBinder {

    @Binds
    @IntoSet
    CommandModule bindAdminCommands(AdminCommands instance);

    @Binds
    @IntoSet
    CommandModule bindWaypointCommands(WaypointCommands instance);

    @Binds
    @IntoSet
    CommandCondition bindIsPlayerCondition(IsPlayer condition);

    @Binds
    @IntoSet
    ArgumentModifier.Factory<?> bindMaxModifierFactory(MaxModifier.Factory factory);
}
