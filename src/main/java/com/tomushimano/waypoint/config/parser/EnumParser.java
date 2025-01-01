package com.tomushimano.waypoint.config.parser;

import com.tomushimano.waypoint.config.ConfigurationParser;
import org.bukkit.configuration.ConfigurationSection;

import static java.util.Objects.requireNonNull;

public class EnumParser<E extends Enum<E>> implements ConfigurationParser<E> {
    private final Class<E> type;

    private EnumParser(final Class<E> type) {
        this.type = requireNonNull(type, "type cannot be null");
    }

    public static <E extends Enum<E>> EnumParser<E> of(final Class<E> type) {
        return new EnumParser<>(type);
    }

    @Override
    public E parse(final ConfigurationSection config, final String key) {
        final String value = config.getString(key);
        return value == null
                ? null
                : Enum.valueOf(this.type, value.toUpperCase());
    }
}
