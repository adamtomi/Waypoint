package com.tomushimano.waypoint.di.module;

import com.tomushimano.waypoint.command.WaypointCommands;
import com.tomushimano.waypoint.command.scaffold.CommandHolder;
import com.tomushimano.waypoint.command.scaffold.condition.IsPlayer;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;
import grapefruit.command.dispatcher.condition.CommandCondition;

@Module
public interface CommandBinder {

    @Binds
    @IntoSet
    CommandHolder bindWaypointCommands(WaypointCommands instance);

    @Binds
    @IntoSet
    CommandCondition bindIsPlayerCondition(IsPlayer condition);
}
