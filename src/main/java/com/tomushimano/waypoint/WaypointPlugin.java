package com.tomushimano.waypoint;

import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.di.WaypointComponent;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Set;

import static com.tomushimano.waypoint.util.ExceptionUtil.capture;
import static com.tomushimano.waypoint.util.IOUtil.copyResourceIfNotExists;
import static java.util.Objects.requireNonNull;

public final class WaypointPlugin extends JavaPlugin {
    private static final Logger LOGGER = NamespacedLoggerFactory.create("Main");
    private final WaypointComponent context;

    WaypointPlugin(final WaypointComponent context) {
        this.context = requireNonNull(context, "context cannot be null");
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
            this.context.configHelper().reloadAll();
        } catch (final IOException ex) {
            abort("Failed to load configuration", ex);
        }

        try {
            LOGGER.info("Setting up database connection...");
            this.context.storageHolder().get().connect();
        } catch (final SQLException ex) {
            abort("Failed to setup database connection", ex);
        }

        this.context.commandService().register(getLifecycleManager());
        registerListeners();

        final long delta = System.currentTimeMillis() - start;
        LOGGER.info("Startup process completed in {} ms", delta);
    }

    @Override
    public void onDisable() {
        final long start = System.currentTimeMillis();
        this.context.navigationService().performShutdown();
        this.context.commandService().unregister();
        this.context.storageHolder().get().disconnect();
        final long delta = System.currentTimeMillis() - start;
        LOGGER.info("Shutdown process completed in {} ms", delta);
    }

    private void registerListeners() {
        LOGGER.info("Registering listeners...");
        final PluginManager pluginManager = getServer().getPluginManager();
        this.context.listeners().forEach(x -> pluginManager.registerEvents(x, this));
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
