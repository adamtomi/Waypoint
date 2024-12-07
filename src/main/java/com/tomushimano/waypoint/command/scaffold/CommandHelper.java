package com.tomushimano.waypoint.command.scaffold;

import com.tomushimano.waypoint.command.scaffold.condition.IsPlayerCondition;
import com.tomushimano.waypoint.command.scaffold.condition.PermissionCondition;
import com.tomushimano.waypoint.command.scaffold.mapper.TextColorArgumentMapper;
import com.tomushimano.waypoint.command.scaffold.mapper.VarcharArgumentMapper;
import com.tomushimano.waypoint.command.scaffold.mapper.WaypointArgumentMapper;
import com.tomushimano.waypoint.core.WaypointService;

import javax.inject.Inject;
import javax.inject.Singleton;

import static java.util.Objects.requireNonNull;

@Singleton
public final class CommandHelper {
    /* Mappers */
    private final WaypointArgumentMapper stdWaypointMapper;
    private final WaypointArgumentMapper ownedWaypointMapper;
    private final TextColorArgumentMapper textColorMapper;
    private final VarcharArgumentMapper varcharArgumentMapper;
    /* Conditions */
    private final IsPlayerCondition isPlayerCondition;
    private final PermissionCondition.Factory permissionConditionFactory;

    @Inject
    public CommandHelper(
            final WaypointArgumentMapper.Factory waypointArgumentMapperFactory,
            final TextColorArgumentMapper textColorMapper,
            final VarcharArgumentMapper.Factory varcharArgumentMapperFactory,
            final IsPlayerCondition isPlayerCondition,
            final PermissionCondition.Factory permissionConditionFactory
    ) {
        this.stdWaypointMapper = waypointArgumentMapperFactory.create(WaypointService::getAccessibleWaypoints);
        this.ownedWaypointMapper = waypointArgumentMapperFactory.create(WaypointService::getOwnedWaypoints);
        this.textColorMapper = textColorMapper;
        this.varcharArgumentMapper = varcharArgumentMapperFactory.create(255);
        this.isPlayerCondition = isPlayerCondition;
        this.permissionConditionFactory = permissionConditionFactory;
    }

    public WaypointArgumentMapper stdWaypoint() {
        return this.stdWaypointMapper;
    }

    public WaypointArgumentMapper ownedWaypoint() {
        return this.ownedWaypointMapper;
    }

    public TextColorArgumentMapper textColor() {
        return this.textColorMapper;
    }

    public VarcharArgumentMapper varchar255() {
        return this.varcharArgumentMapper;
    }

    public IsPlayerCondition isPlayer() {
        return this.isPlayerCondition;
    }

    public PermissionCondition perm(final String permission) {
        requireNonNull(permission, "permission cannot be null");
        return this.permissionConditionFactory.create(permission);
    }
}
