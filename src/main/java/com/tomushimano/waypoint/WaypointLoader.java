package com.tomushimano.waypoint;

import com.tomushimano.waypoint.command.CommandManager;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;

import static com.tomushimano.waypoint.util.ExceptionUtil.capture;
import static com.tomushimano.waypoint.util.IOUtil.copyResourceIfNotExists;

public final class WaypointLoader {
    private static final Logger LOGGER = NamespacedLoggerFactory.create("Main");
    private final JavaPlugin plugin;
    private final CommandManager commandManager;

    @Inject
    public WaypointLoader(JavaPlugin plugin, CommandManager commandManager) {
        this.plugin = plugin;
        this.commandManager = commandManager;
    }

    public void load() {
        try {
            LOGGER.info("Attempting to copy configuration files...");
            copyResources();
        } catch (IOException ex) {
            capture(ex, "Failed to copy configuration files", LOGGER);
        }

        this.commandManager.register();
    }

    public void unload() {
        this.commandManager.shutdown();
    }

    private void copyResources() throws IOException {
        Path datafolder = this.plugin.getDataPath();
        copyResourceIfNotExists(datafolder, "config.yml");
    }
}
