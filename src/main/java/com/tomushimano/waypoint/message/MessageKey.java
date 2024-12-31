package com.tomushimano.waypoint.message;

import com.tomushimano.waypoint.config.ConfigKey;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.slf4j.Logger;

import static java.util.Objects.requireNonNull;

final class MessageKey implements ConfigKey<String> {
    private static final Logger LOGGER = NamespacedLoggerFactory.create(MessageKey.class);
    private final String key;

    private MessageKey(final String key) {
        this.key = requireNonNull(key, "key cannot be null");
    }

    static MessageKey messageKey(final String key) {
        return new MessageKey(key);
    }

    @Override
    public String key() {
        return this.key;
    }

    @Override
    public String readFrom(final ConfigurationSection config) {
        final String value = config.getString(this.key);
        if (value == null) {
            LOGGER.warn("Message key \"{}\" was not found in the translation file.", this.key);
            return this.key;
        }

        return value;
    }
}
