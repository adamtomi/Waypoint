package com.tomushimano.waypoint.message;

import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.core.Waypoint;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static java.util.Objects.requireNonNull;

public interface MessageBuilder {
    Preset1<Waypoint> WAYPOINT_CREATION_SUCCESS = keyed("waypoint.creation_success")
            .var1((raw, waypoint) -> raw.replace("{name}", waypoint.getName())
                    .replace("{x}", String.valueOf(waypoint.getPosition().getX()))
                    .replace("{y}", String.valueOf(waypoint.getPosition().getY()))
                    .replace("{z}", String.valueOf(waypoint.getPosition().getZ())));

    Preset0 var0();

    <V> Preset1<V> var1(final BiFunction<String, V, String> mapper);

    <V0, V1> Preset2<V0, V1> var2(final Configurable config, final V0 variable0, final V1 variable1);

    String v(final String name, final String variable);

    static MessageBuilder keyed(final String key) {
        requireNonNull(key, "key cannot be null");
        throw new UnsupportedOperationException();
    }

    interface Preset0 {

        Message with(final Configurable config);
    }

    interface Preset1<V0> {

        Message with(final Configurable config, final V0 variable0);
    }

    interface Preset2<V0, V1> {

        Message with(final Configurable config, final V0 variable0, final V1 variable1);
    }
}
