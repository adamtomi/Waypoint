package com.tomushimano.waypoint.config.message;

import com.tomushimano.waypoint.config.ConfigKey;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.di.qualifier.Lang;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MessageConfig {
    private final Configurable config;

    @Inject
    public MessageConfig(final @Lang Configurable config) {
        this.config = config;
    }

    public MessageBuilder get(final ConfigKey<String> key) {
        return new MessageBuilder(this.config.get(key));
    }
}
