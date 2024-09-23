package com.tomushimano.waypoint.di.module;

import com.tomushimano.waypoint.command.AdminCommands;
import com.tomushimano.waypoint.command.WaypointCommands;
import com.tomushimano.waypoint.command.scaffold.CommandHolder;
import com.tomushimano.waypoint.command.scaffold.condition.IsPlayer;
import com.tomushimano.waypoint.command.scaffold.modifier.MaxModifier;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;
import grapefruit.command.argument.modifier.ContextualModifier;
import grapefruit.command.dispatcher.condition.CommandCondition;

@Module
public interface CommandBinder {

    @Binds
    @IntoSet
    CommandHolder bindAdminCommands(AdminCommands instance);

    @Binds
    @IntoSet
    CommandHolder bindWaypointCommands(WaypointCommands instance);

    @Binds
    @IntoSet
    CommandCondition bindIsPlayerCondition(IsPlayer condition);

    @Binds
    @IntoSet
    ContextualModifier.Factory<?> bindMaxModifierFactory(MaxModifier.Factory factory);
}
