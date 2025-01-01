package com.tomushimano.waypoint.config;

import com.google.common.base.MoreObjects;
import org.bukkit.configuration.ConfigurationSection;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public final class MappedConfigKey<I, O> extends AbstractConfigKey<O> {
    private final ConfigKey<I> delegate;
    private final Function<I, O> mapper;

    public MappedConfigKey(final ConfigKey<I> delegate, final Function<I, O> mapper) {
        super(delegate.key());
        this.delegate = requireNonNull(delegate, "key cannot be null");
        this.mapper = requireNonNull(mapper, "mapper cannot be null");
    }

    @Override
    public O parse(final ConfigurationSection config) {
        final I value = this.delegate.parse(config);
        return this.mapper.apply(value);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("delegate", this.delegate)
                .toString();
    }
}
