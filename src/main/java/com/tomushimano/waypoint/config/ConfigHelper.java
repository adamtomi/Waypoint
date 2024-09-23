package com.tomushimano.waypoint.config;

import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Set;

import static com.tomushimano.waypoint.util.ExceptionUtil.capture;

@Singleton
public class ConfigHelper {
    private static final Logger LOGGER = NamespacedLoggerFactory.create(ConfigHelper.class);
    private final Set<ConfigHolder> configurations;

    @Inject
    public ConfigHelper(Set<ConfigHolder> configurations) {
        this.configurations = configurations;
    }

    public boolean reloadAll() {
        for (ConfigHolder config : this.configurations) {
            try {
                config.reload();
            } catch (IOException ex) {
                capture(ex, "Failed to load configuration from file: '%s'".formatted(config.getBackingFile()), LOGGER);
                return false;
            }
        }

        return true;
    }
}
