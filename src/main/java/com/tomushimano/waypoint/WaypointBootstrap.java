package com.tomushimano.waypoint;

import com.tomushimano.waypoint.di.DaggerWaypointComponent;
import com.tomushimano.waypoint.di.WaypointComponent;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class WaypointBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(final BootstrapContext context) {}

    @Override
    public JavaPlugin createPlugin(final PluginProviderContext context) {
        final WaypointComponent component = DaggerWaypointComponent.builder()
                .dataDir(context.getDataDirectory())
                .build();

        return component.plugin();
    }
}
