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
    public void bootstrap(@NotNull BootstrapContext context) {
        // TODO mojang command registration
    }

    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        // Very nice solution, yay, much wow.
        JavaPlugin plugin = new WaypointPlugin(
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
