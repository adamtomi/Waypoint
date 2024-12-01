package com.tomushimano.waypoint.command.scaffold;

import grapefruit.command.runtime.annotation.meta.InjectedBy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.tomushimano.waypoint.command.scaffold.WaypointContextKeys.SENDER_KEY_NAME;

@Deprecated
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.PARAMETER)
@InjectedBy(value = SENDER_KEY_NAME, nullable = false)
public @interface Sender {}
