package com.tomushimano.waypoint.command.scaffold;

import com.tomushimano.waypoint.command.scaffold.condition.InWorldCondition;
import com.tomushimano.waypoint.command.scaffold.condition.IsPlayerCondition;
import com.tomushimano.waypoint.command.scaffold.condition.PermissionCondition;
import com.tomushimano.waypoint.command.scaffold.mapper.IntArgumentMapper;
import com.tomushimano.waypoint.command.scaffold.mapper.NameArgumentMapper;
import com.tomushimano.waypoint.command.scaffold.mapper.TextColorArgumentMapper;
import com.tomushimano.waypoint.command.scaffold.mapper.WaypointArgumentMapper;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.core.WaypointService;
import grapefruit.command.util.key.Key;

import javax.inject.Inject;
import javax.inject.Singleton;

import static java.util.Objects.requireNonNull;

@Singleton
public final class CommandHelper {
    /* Mappers */
    private final IntArgumentMapper.Factory intArgumentMapperFactory;
    private final WaypointArgumentMapper stdWaypointMapper;
    private final WaypointArgumentMapper ownedWaypointMapper;
    private final TextColorArgumentMapper textColorMapper;
    private final NameArgumentMapper nameArgumentMapper;
    /* Conditions */
    private final InWorldCondition.Factory inWorldFactory;
    private final IsPlayerCondition isPlayerCondition;
    private final PermissionCondition.Factory permissionConditionFactory;

    @Inject
    public CommandHelper(
            final IntArgumentMapper.Factory intArgumentMapperFactory,
            final WaypointArgumentMapper.Factory waypointArgumentMapperFactory,
            final TextColorArgumentMapper textColorMapper,
            final NameArgumentMapper nameArgumentMapper,
            final InWorldCondition.Factory inWorldFactory,
            final IsPlayerCondition isPlayerCondition,
            final PermissionCondition.Factory permissionConditionFactory
    ) {
        this.intArgumentMapperFactory = intArgumentMapperFactory;
        this.stdWaypointMapper = waypointArgumentMapperFactory.create(WaypointService::getAccessibleWaypoints);
        this.ownedWaypointMapper = waypointArgumentMapperFactory.create(WaypointService::getOwnedWaypoints);
        this.textColorMapper = textColorMapper;
        this.nameArgumentMapper = nameArgumentMapper;
        this.inWorldFactory = inWorldFactory;
        this.isPlayerCondition = isPlayerCondition;
        this.permissionConditionFactory = permissionConditionFactory;
    }

    public IntArgumentMapper positiveInt() {
        return this.intArgumentMapperFactory.create(1, Integer.MAX_VALUE);
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

    public NameArgumentMapper name() {
        return this.nameArgumentMapper;
    }

    public IsPlayerCondition isPlayer() {
        return this.isPlayerCondition;
    }

    public PermissionCondition perm(final String permission) {
        requireNonNull(permission, "permission cannot be null");
        return this.permissionConditionFactory.create(permission);
    }

    public InWorldCondition inWorld(final Key<Waypoint> waypointKey) {
        requireNonNull(waypointKey, "waypointKey cannot be null");
        return this.inWorldFactory.create(waypointKey);
    }
}
