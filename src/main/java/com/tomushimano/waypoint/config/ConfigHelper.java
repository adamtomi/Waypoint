package com.tomushimano.waypoint.config;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Set;

@Singleton
public class ConfigHelper {
    private final Set<Configurable> configurations;

    @Inject
    public ConfigHelper(final Set<Configurable> configurations) {
        this.configurations = configurations;
    }

    public void reloadAll() throws IOException {
        for (final Configurable config : this.configurations) {
            config.reload();
        }
    }
}
