package com.tomushimano.waypoint.config;

import java.io.IOException;
import java.nio.file.Path;

public interface Configurable {
    String COMMAND_YML = "command.yml";
    String CONFIG_YML = "config.yml";
    String LANG_YML = "lang.yml";

    <T> T get(final ConfigKey<T> key);

    void reload() throws IOException;

    Path getBackingFile();

    static Configurable fileBacked(final Path file) {
        return new ConfigurableImpl(file);
    }
}
