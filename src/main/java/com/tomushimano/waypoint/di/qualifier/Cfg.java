package com.tomushimano.waypoint.di.qualifier;

import javax.inject.Qualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Qualifier
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.METHOD, ElementType.PARAMETER })
public @interface Cfg {}
