package com.tomushimano.waypoint.config;

import org.bukkit.configuration.ConfigurationSection;

import java.util.function.BiFunction;

import static java.util.Objects.requireNonNull;

final class SimpleConfigKey<T> implements ConfigKey<T> {
    private final String key;
    private final BiFunction<ConfigurationSection, String, T> parser;

    SimpleConfigKey(String key, BiFunction<ConfigurationSection, String, T> parser) {
        this.key = requireNonNull(key, "key cannot be null");
        this.parser = requireNonNull(parser, "parser cannot be null");
    }

    @Override
    public String key() {
        return this.key;
    }

    @Override
    public T readFrom(ConfigurationSection config) {
        T result = this.parser.apply(config, this.key);
        if (result == null) throw new IllegalArgumentException("ConfigurationSection does not have key: '%s'".formatted(this.key));

        return result;
    }

    @Override
    public String toString() {
        return "SimpleConfigKey{key=%s}".formatted(this.key);
    }
}
