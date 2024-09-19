package com.tomushimano.waypoint.config;

import java.io.IOException;
import java.nio.file.Path;

public interface ConfigHolder {
    String COMMAND_YML = "command.yml";
    String CONFIG_YML = "config.yml";
    String LANG_YML = "lang.yml";

    <T> T get(ConfigKey<T> key);

    void reload() throws IOException;

    Path getBackingFile();

    static ConfigHolder fileBacked(Path file) {
        return new ConfigHolderImpl(file);
    }
}
