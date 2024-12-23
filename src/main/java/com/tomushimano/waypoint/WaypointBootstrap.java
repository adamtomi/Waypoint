package com.tomushimano.waypoint;

import com.tomushimano.waypoint.di.DaggerWaypointComponent;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class WaypointBootstrap implements PluginBootstrap {
    private WaypointLoader loader;

    @Override
    public void bootstrap(final @NotNull BootstrapContext context) {}

    @Override
    public @NotNull JavaPlugin createPlugin(final @NotNull PluginProviderContext context) {
        // Very nice solution, yay, much wow.
        final JavaPlugin plugin = new WaypointPlugin(
                () -> this.loader.load(),
                () -> this.loader.unload()
        );

        this.loader = DaggerWaypointComponent.builder()
                .plugin(plugin)
                .build()
                .instance();

        return plugin;
    }
}
