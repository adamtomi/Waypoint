package com.tomushimano.waypoint.util;

import com.tomushimano.waypoint.config.Configurable;
import net.kyori.adventure.text.Component;

public interface VerboseException {

    Component describeFailure(final Configurable config);

    @FunctionalInterface
    interface ComponentProvider {

        Component provide(final Configurable config);
    }
}
