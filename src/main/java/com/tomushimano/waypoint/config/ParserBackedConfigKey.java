package com.tomushimano.waypoint.config;

import com.google.common.base.MoreObjects;
import org.bukkit.configuration.ConfigurationSection;

import static java.util.Objects.requireNonNull;

public final class ParserBackedConfigKey<T> extends AbstractConfigKey<T> {
    private final ConfigurationParser<T> parser;

    public ParserBackedConfigKey(final String key, final ConfigurationParser<T> parser) {
        super(key);
        this.parser = requireNonNull(parser, "parser cannot be null");
    }

    @Override
    public T parse(final ConfigurationSection config) {
        return this.parser.parse(config, key());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("key", key())
                .add("parser", this.parser)
                .toString();
    }
}
