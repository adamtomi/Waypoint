package com.tomushimano.waypoint.core.navigation;

import com.tomushimano.waypoint.core.Waypoint;

import java.util.UUID;

public record Navigation(UUID uniqueId, Waypoint destination, ParticleStream stream, Runnable arrivalHook) {}
