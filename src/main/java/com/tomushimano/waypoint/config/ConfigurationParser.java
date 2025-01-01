package com.tomushimano.waypoint.config;

import org.bukkit.configuration.ConfigurationSection;

@FunctionalInterface
public interface ConfigurationParser<T> {

    T parse(final ConfigurationSection config, final String key);

    static <T> ConfigurationParser<T> strictParser(final ConfigurationParser<T> delegate) {
        return (config, key) -> {
            final T value = delegate.parse(config, key);
            if (value == null) {
                throw new MissingConfigurationEntryException(key, config.getCurrentPath());
            }

            return value;
        };
    }
}
