package com.tomushimano.waypoint.config;

import com.google.common.base.MoreObjects;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.slf4j.Logger;

import static java.util.Objects.requireNonNull;

public final class FallbackConfigKey<T> extends AbstractConfigKey<T> {
    private static final Logger LOGGER = NamespacedLoggerFactory.create(FallbackConfigKey.class);
    private final ParserBackedConfigKey<T> delegate;
    private final T fallback;

    public FallbackConfigKey(final ParserBackedConfigKey<T> delegate, final T fallback) {
        super(delegate.key());
        this.delegate = requireNonNull(delegate, "delegate cannot be null");
        this.fallback = requireNonNull(fallback, "fallback cannot be null");
    }

    @Override
    public T parse(final ConfigurationSection config) {
        final T value = this.delegate.parse(config);
        if (value == null) {
            LOGGER.warn("Unrecognized configuration key: \"{}\"", key());
            return this.fallback;
        }

        return value;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("delegate", this.delegate)
                .add("fallback", this.fallback)
                .toString();
    }
}
