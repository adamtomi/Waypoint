package com.tomushimano.waypoint.message;

import com.tomushimano.waypoint.config.Configurable;

import static com.tomushimano.waypoint.message.MessageKey.messageKey;
import static java.util.Objects.requireNonNull;

public interface MessageBuilder {

    Preset0 var0();

    <V> Preset1<V> var1(final Processor1<V> processor);

    <V0, V1> Preset2<V0, V1> var2(final Processor2<V0, V1> processor);

    static MessageBuilder keyed(final String key) {
        requireNonNull(key, "key cannot be null");
        return new MessageBuilderImpl(messageKey(key));
    }

    interface Preset0 {

        Message from(final Configurable config);
    }

    interface Processor1<V> {

        String apply(final String raw, final V var0);
    }

    interface Preset1<V> {

        Message with(final Configurable config, final V var0);
    }

    interface Processor2<V0, V1> {

        String apply(final String raw, final V0 var0, final V1 var1);
    }

    interface Preset2<V0, V1> {

        Message with(final Configurable config, final V0 var0, final V1 var1);
    }
}
