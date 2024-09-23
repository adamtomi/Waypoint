package com.tomushimano.waypoint.command.scaffold.modifier;

import grapefruit.command.annotation.modifier.Modifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.PARAMETER)
@Modifier.Factory(MaxModifier.Factory.class)
public @interface Max {

    int value();
}
