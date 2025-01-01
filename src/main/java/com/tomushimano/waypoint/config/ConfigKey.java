package com.tomushimano.waypoint.config;

import com.tomushimano.waypoint.config.parser.EnumParser;
import org.bukkit.configuration.ConfigurationSection;

import java.util.function.Function;

public interface ConfigKey<T> {

    String key();

    T parse(final ConfigurationSection config);

    <O> ConfigKey<O> then(final Function<T, O> mapper);

    static <T> ConfigKey<T> strictKey(final String key, final ConfigurationParser<T> parser) {
        return new ParserBackedConfigKey<>(key, ConfigurationParser.strictParser(parser));
    }

    static ConfigKey<String> stringKey(final String key) {
        return strictKey(key, ConfigurationSection::getString);
    }

    static ConfigKey<Integer> intKey(final String key) {
        return strictKey(key, ConfigurationSection::getInt);
    }

    static ConfigKey<Double> doubleKey(final String key) {
        return strictKey(key, ConfigurationSection::getDouble);
    }

    static ConfigKey<Float> floatKey(final String key) {
        return doubleKey(key).then(Number::floatValue);
    }

    static ConfigKey<Boolean> boolKey(final String key) {
        return strictKey(key, ConfigurationSection::getBoolean);
    }

    static <E extends Enum<E>> ConfigKey<E> enumKey(final String key, final Class<E> type) {
        return strictKey(key, EnumParser.of(type));
    }

    // Config key that defaults to the key name itself if the backing parser returns null
    static ConfigKey<String> fallbackToKey(final String key) {
        return new FallbackConfigKey<>(new ParserBackedConfigKey<>(key, ConfigurationSection::getString), key);
    }
}
