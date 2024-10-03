package com.tomushimano.waypoint.config;

import org.bukkit.configuration.ConfigurationSection;

import java.util.function.BiFunction;

public interface ConfigKey<T> {

    String key();

    T readFrom(ConfigurationSection config);

    static <T> ConfigKey<T> simpleKey(String key, BiFunction<ConfigurationSection, String, T> parser) {
        return new SimpleConfigKey<>(key, parser);
    }

    static ConfigKey<String> stringKey(String key) {
        return simpleKey(key, ConfigurationSection::getString);
    }

    static ConfigKey<Integer> integerKey(String key) {
        return simpleKey(key, ConfigurationSection::getInt);
    }

    static ConfigKey<Double> doubleKey(String key) {
        return simpleKey(key, ConfigurationSection::getDouble);
    }

    static <E extends Enum<E>> ConfigKey<E> enumKey(String key, Class<E> type) {
        return simpleKey(key, (section, x) -> {
            String value = section.getString(x);
            return value == null
                    ? null
                    : Enum.valueOf(type, value.toUpperCase());
        });
    }
}
