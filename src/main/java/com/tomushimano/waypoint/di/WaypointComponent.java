package com.tomushimano.waypoint.di;

import com.tomushimano.waypoint.WaypointLoader;
import com.tomushimano.waypoint.di.module.CommandBinder;
import dagger.BindsInstance;
import dagger.Component;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Singleton;

@Singleton
@Component(modules = CommandBinder.class)
public interface WaypointComponent {

    WaypointLoader instance();

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder plugin(JavaPlugin plugin);

        WaypointComponent build();
    }
}
