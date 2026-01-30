package com.tomushimano.waypoint;

import com.tomushimano.waypoint.command.CommandService;
import com.tomushimano.waypoint.config.ConfigHelper;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.core.navigation.NavigationService;
import com.tomushimano.waypoint.datastore.StorageHolder;
import com.tomushimano.waypoint.util.FutureFactory;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Set;

import static com.tomushimano.waypoint.util.ExceptionUtil.capture;
import static com.tomushimano.waypoint.util.IOUtil.copyResourceIfNotExists;

public final class WaypointPlugin extends JavaPlugin {
    private static final Logger LOGGER = NamespacedLoggerFactory.create("Main");
    private final ConfigHelper configHelper;
    private final StorageHolder storageHolder;
    private final CommandService commandService;
    private final NavigationService navigationService;
    private final FutureFactory futureFactory;
    private final Set<Listener> listeners;

    @Inject
    public WaypointPlugin(
            final ConfigHelper configHelper,
            final StorageHolder storageHolder,
            final CommandService commandService,
            final NavigationService navigationService,
            final FutureFactory futureFactory,
            final Set<Listener> listeners
    ) {
        this.configHelper = configHelper;
        this.storageHolder = storageHolder;
        this.commandService = commandService;
        this.navigationService = navigationService;
        this.futureFactory = futureFactory;
        this.listeners = listeners;
    }

    @Override
    public void onEnable() {
        final long start = System.currentTimeMillis();
        try {
            LOGGER.info("Attempting to copy configuration files...");
            copyResources();
        } catch (final IOException ex) {
            abort("Failed to copy configuration files", ex);
        }

        try {
            LOGGER.info("Loading configuration...");
            this.configHelper.reloadAll();
        } catch (final IOException ex) {
            abort("Failed to load configuration", ex);
        }

        try {
            LOGGER.info("Setting up database connection...");
            this.storageHolder.get().connect();
        } catch (final SQLException ex) {
            abort("Failed to setup database connection", ex);
        }

        this.commandService.register(getLifecycleManager());
        registerListeners();

        final long delta = System.currentTimeMillis() - start;
        LOGGER.info("Startup process completed in {} ms", delta);
    }

    @Override
    public void onDisable() {
        final long start = System.currentTimeMillis();
        this.navigationService.performShutdown();
        this.commandService.unregister();
        this.storageHolder.get().disconnect();

        LOGGER.info("Shutting down async executor, waiting for pending tasks (up to 1 second)");
        this.futureFactory.shutdown(1L);
        LOGGER.info("Done!");

        final long delta = System.currentTimeMillis() - start;
        LOGGER.info("Shutdown process completed in {} ms", delta);
    }

    private void registerListeners() {
        LOGGER.info("Registering listeners...");
        final PluginManager pluginManager = getServer().getPluginManager();
        this.listeners.forEach(x -> pluginManager.registerEvents(x, this));
    }

    private void copyResources() throws IOException {
        final Path datafolder = getDataPath();
        final Set<String> files = Set.of(
                Configurable.COMMAND_YML,
                Configurable.CONFIG_YML,
                Configurable.LANG_YML
        );

        for (final String file : files) copyResourceIfNotExists(datafolder, file);
    }

    private void abort(final String message, final Throwable ex) {
        capture(ex, message, LOGGER);
        throw new RuntimeException("Failed to enable plugin!", ex);
    }
}
