package com.tomushimano.waypoint.command.scaffold.mapper;

import com.tomushimano.waypoint.core.WaypointService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
/* Holds our custom argument mapper instances */
public final class ArgumentMapperHolder {
    private final WaypointArgumentMapper stdWaypointMapper;
    private final WaypointArgumentMapper ownWaypointMapper;
    private final TextColorArgumentMapper textColorMapper;
    private final VarcharArgumentMapper varcharArgumentMapper;

    @Inject
    public ArgumentMapperHolder(
            final WaypointArgumentMapper.Factory waypointArgumentMapperFactory,
            final TextColorArgumentMapper textColorMapper,
            final VarcharArgumentMapper.Factory varcharArgumentMapperFactory
    ) {
        this.stdWaypointMapper = waypointArgumentMapperFactory.create(WaypointService::getAccessibleWaypoints);
        this.ownWaypointMapper = waypointArgumentMapperFactory.create(WaypointService::getOwnedWaypoints);
        this.textColorMapper = textColorMapper;
        this.varcharArgumentMapper = varcharArgumentMapperFactory.create(255);
    }

    public WaypointArgumentMapper stdWaypoint() {
        return this.stdWaypointMapper;
    }

    public WaypointArgumentMapper ownWaypoint() {
        return this.ownWaypointMapper;
    }

    public TextColorArgumentMapper textColor() {
        return this.textColorMapper;
    }

    public VarcharArgumentMapper varchar255() {
        return this.varcharArgumentMapper;
    }
}
