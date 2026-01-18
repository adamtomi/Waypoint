package com.tomushimano.waypoint.di.module;

import com.tomushimano.waypoint.command.scaffold.mapper.WaypointArgumentMapper;
import com.tomushimano.waypoint.core.WaypointService;
import com.tomushimano.waypoint.di.qualifier.Accessible;
import com.tomushimano.waypoint.di.qualifier.Own;
import dagger.Module;
import dagger.Provides;

@Module
public class CommandProvider {

    @Own
    @Provides
    public WaypointArgumentMapper provideOwn(final WaypointService service) {
        return new WaypointArgumentMapper(WaypointService::getOwnedWaypoints, service);
    }

    @Accessible
    @Provides
    public WaypointArgumentMapper provideAccessible(final WaypointService service) {
        return new WaypointArgumentMapper(WaypointService::getAccessibleWaypoints, service);
    }
}
