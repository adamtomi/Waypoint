package com.tomushimano.waypoint.core.navigation;

import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.config.StandardKeys;
import org.bukkit.Color;

import static com.tomushimano.waypoint.util.DoubleCheck.requireNonNegative;
import static com.tomushimano.waypoint.util.DoubleCheck.requirePositive;
import static java.util.Objects.requireNonNull;

public record ParticleConfig(Color color, int count, int density, int size, int extraHeight) {
    public ParticleConfig {
        requireNonNull(color, "color cannot be null");
        requirePositive(count, "count must be positive");
        requirePositive(density, "density must be positive");
        requirePositive(size, "size must be positive");
        requireNonNegative(extraHeight, "extraHeight cannot be null");
    }

    public static ParticleConfig from(final Configurable config) {
        return new ParticleConfig(
                config.get(StandardKeys.Navigation.PARTICLE_COLOR),
                config.get(StandardKeys.Navigation.PARTICLE_COUNT),
                config.get(StandardKeys.Navigation.PARTICLE_DENSITY),
                config.get(StandardKeys.Navigation.PARTICLE_SIZE),
                config.get(StandardKeys.Navigation.PARTICLE_Y_OFFSET)
        );
    }
}
