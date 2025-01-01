package com.tomushimano.waypoint.config;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public abstract class AbstractConfigKey<T> implements ConfigKey<T> {
    private final String key;

    protected AbstractConfigKey(final String key) {
        this.key = requireNonNull(key, "key cannot be null");
    }

    @Override
    public String key() {
        return this.key;
    }

    @Override
    public <O> ConfigKey<O> then(final Function<T, O> mapper) {
        return new MappedConfigKey<>(this, mapper);
    }
}
