package com.tomushimano.waypoint.core;

import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.config.StandardKeys;
import com.tomushimano.waypoint.di.qualifier.Cfg;
import com.tomushimano.waypoint.util.Position;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

@Singleton
public final class LightSourceFactory {
    private final Configurable config;

    @Inject
    public LightSourceFactory(final @Cfg Configurable config) {
        this.config = config;
    }

    public LightSource create(final Waypoint waypoint) {
        final Supplier<Position> position = () -> waypoint.getPosition().plus(0.0D, this.config.get(StandardKeys.LightSource.OFFSET_Y), 0.0D);
        final IntSupplier level = () -> this.config.get(StandardKeys.LightSource.LEVEL);

        return new LightSourceImpl(position, level);
    }
}
