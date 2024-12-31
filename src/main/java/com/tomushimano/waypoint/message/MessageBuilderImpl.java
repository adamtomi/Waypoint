package com.tomushimano.waypoint.message;

import com.tomushimano.waypoint.config.ConfigKey;

import static java.util.Objects.requireNonNull;

final class MessageBuilderImpl implements MessageBuilder {
    private final ConfigKey<String> key;

    MessageBuilderImpl(final ConfigKey<String> key) {
        this.key = requireNonNull(key, "key cannot be null");
    }

    private static Message message(final String message) {
        return Message.of(message);
    }

    @Override
    public Preset0 var0() {
        return config -> message(config.get(this.key));
    }

    @Override
    public <V> Preset1<V> var1(final Processor1<V> processor) {
        return (config, var0) -> message(processor.apply(config.get(this.key), var0));
    }

    @Override
    public <V0, V1> Preset2<V0, V1> var2(final Processor2<V0, V1> processor) {
        return (config, var0, var1) -> message(processor.apply(config.get(this.key), var0, var1));
    }

    @Override
    public <V0, V1, V2> Preset3<V0, V1, V2> var3(final Processor3<V0, V1, V2> processor) {
        return (config, var0, var1, var2) -> message(processor.apply(config.get(this.key), var0, var1, var2));
    }
}
