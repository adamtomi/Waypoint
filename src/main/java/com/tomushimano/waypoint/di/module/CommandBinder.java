package com.tomushimano.waypoint.di.module;

import com.tomushimano.waypoint.command.WaypointCommands;
import com.tomushimano.waypoint.command.scaffold.CommandHolder;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;

@Module
public interface CommandBinder {

    @Binds
    @IntoSet
    CommandHolder bindWaypointCommands(WaypointCommands instance);
}
