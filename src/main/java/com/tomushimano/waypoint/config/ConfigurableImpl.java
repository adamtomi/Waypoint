package com.tomushimano.waypoint.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

final class ConfigurableImpl implements Configurable {
    private final Map<ConfigKey<?>, Object> cache = new HashMap<>();
    private final Path file;
    private final YamlConfiguration internalConfig;

    ConfigurableImpl(Path file) {
        this.file = file;
        this.internalConfig = new YamlConfiguration();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(ConfigKey<T> key) {
        if (this.cache.containsKey(key)) return (T) this.cache.get(key);

        T result = key.readFrom(this.internalConfig);
        this.cache.put(key, result);
        return result;
    }

    @Override
    public void reload() throws IOException {
        try {
            // Wipe cache
            this.cache.clear();
            // Load configuration from file
            this.internalConfig.load(this.file.toFile());
        } catch (InvalidConfigurationException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public Path getBackingFile() {
        return this.file;
    }
}
