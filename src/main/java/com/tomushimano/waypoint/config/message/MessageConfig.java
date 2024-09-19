package com.tomushimano.waypoint.config.message;

import com.tomushimano.waypoint.config.ConfigHolder;
import com.tomushimano.waypoint.config.ConfigKey;
import com.tomushimano.waypoint.di.qualifier.Lang;

import javax.inject.Inject;

public class MessageConfig {
    private final ConfigHolder config;

    @Inject
    public MessageConfig(@Lang ConfigHolder config) {
        this.config = config;
    }

    public MessageBuilder get(ConfigKey<String> key) {
        return new MessageBuilder(this.config.get(key));
    }
}
