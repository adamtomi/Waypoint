package com.tomushimano.waypoint.config;

import org.bukkit.configuration.ConfigurationSection;

import java.util.function.BiFunction;

public interface ConfigKey<T> {

    String key();

    T readFrom(final ConfigurationSection config);

    static <T> ConfigKey<T> simpleKey(final String key, final BiFunction<ConfigurationSection, String, T> parser) {
        return new SimpleConfigKey<>(key, parser);
    }

    static ConfigKey<String> stringKey(final String key) {
        return simpleKey(key, ConfigurationSection::getString);
    }

    static ConfigKey<Integer> integerKey(final String key) {
        return simpleKey(key, ConfigurationSection::getInt);
    }

    static ConfigKey<Double> doubleKey(final String key) {
        return simpleKey(key, ConfigurationSection::getDouble);
    }

    static ConfigKey<Float> floatKey(final String key) {
        return simpleKey(key, ($config, $key) -> {
            final double d = $config.getDouble($key);
            return (float) d;
        });
    }

    static <E extends Enum<E>> ConfigKey<E> enumKey(final String key, final Class<E> type) {
        return simpleKey(key, (section, x) -> {
            String value = section.getString(x);
            return value == null
                    ? null
                    : Enum.valueOf(type, value.toUpperCase());
        });
    }
}
