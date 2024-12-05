package com.tomushimano.waypoint.command.scaffold.mapper;

import com.tomushimano.waypoint.core.WaypointService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
/* Holds our custom argument mapper instances */
public class ArgumentMapperHolder {
    private final WaypointArgumentMapper stdWaypointMapper;
    private final WaypointArgumentMapper ownWaypointMapper;
    private final TextColorArgumentMapper textColorMapper;

    @Inject
    public ArgumentMapperHolder(
            final WaypointArgumentMapper.Factory waypointArgumentMapperFactory,
            final TextColorArgumentMapper textColorMapper
    ) {
        this.stdWaypointMapper = waypointArgumentMapperFactory.create(WaypointService::getAccessibleWaypoints);
        this.ownWaypointMapper = waypointArgumentMapperFactory.create(WaypointService::getOwnedWaypoints);
        this.textColorMapper = textColorMapper;
    }

    public WaypointArgumentMapper stdWaypointMapper() {
        return this.stdWaypointMapper;
    }

    public WaypointArgumentMapper ownWaypointMapper() {
        return this.ownWaypointMapper;
    }

    public TextColorArgumentMapper textColorMapper() {
        return this.textColorMapper;
    }
}
