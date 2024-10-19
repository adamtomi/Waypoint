package com.tomushimano.waypoint.command.scaffold;

import grapefruit.command.annotation.meta.MappedBy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.tomushimano.waypoint.command.scaffold.mapper.WaypointArgumentMapper.OWNING_NAME;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.PARAMETER)
@MappedBy(OWNING_NAME)
public @interface Owning {}
