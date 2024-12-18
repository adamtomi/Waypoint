package com.tomushimano.waypoint;

import com.tomushimano.waypoint.command.CommandManager;
import com.tomushimano.waypoint.config.ConfigHelper;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.core.navigation.NavigationService;
import com.tomushimano.waypoint.datastore.StorageHolder;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

import static com.tomushimano.waypoint.util.ExceptionUtil.capture;
import static com.tomushimano.waypoint.util.IOUtil.copyResourceIfNotExists;

public final class WaypointLoader {
    private static final Logger LOGGER = NamespacedLoggerFactory.create("Main");
    private final JavaPlugin plugin;
    private final CommandManager commandManager;
    private final Set<Listener> listeners;
    private final ConfigHelper configHelper;
    private final StorageHolder storageHolder;
    private final NavigationService navigationService;

    @Inject
    public WaypointLoader(
            final JavaPlugin plugin,
            final CommandManager commandManager,
            final Set<Listener> listeners,
            final ConfigHelper configHelper,
            final StorageHolder storageHolder,
            final NavigationService navigationService
    ) {
        this.plugin = plugin;
        this.commandManager = commandManager;
        this.listeners = listeners;
        this.configHelper = configHelper;
        this.storageHolder = storageHolder;
        this.navigationService = navigationService;
    }

    public void load() {
        try {
            LOGGER.info("Attempting to copy configuration files...");
            copyResources();
        } catch (final IOException ex) {
            capture(ex, "Failed to copy configuration files", LOGGER);
            fail();
        }

        // Load configuration into every config holder
        if (!this.configHelper.reloadAll()) {
            fail();
        }

        // Could not establish a connection to database, return here
        if (!this.storageHolder.get().connect()) fail();

        this.commandManager.register();
        registerListeners();
    }

    public void unload() {
        this.navigationService.performShutdown();
        this.commandManager.shutdown();
        this.storageHolder.get().disconnect();
    }

    private void copyResources() throws IOException {
        final Path datafolder = this.plugin.getDataPath();
        final Set<String> files = Set.of(
                Configurable.COMMAND_YML,
                Configurable.CONFIG_YML,
                Configurable.LANG_YML
        );

        for (final String file : files) copyResourceIfNotExists(datafolder, file);
    }

    private void registerListeners() {
        LOGGER.info("Registering listeners...");
        final PluginManager pluginManager = this.plugin.getServer().getPluginManager();
        this.listeners.forEach(x -> pluginManager.registerEvents(x, this.plugin));
    }

    private void fail() {
        throw new RuntimeException("Failed to load plugin due to an unexpected error.");
    }
}
