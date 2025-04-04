package com.tomushimano.waypoint.di;

import com.tomushimano.waypoint.WaypointLoader;
import com.tomushimano.waypoint.di.module.CommandBinder;
import com.tomushimano.waypoint.di.module.ConfigBinder;
import com.tomushimano.waypoint.di.module.ConfigProvider;
import com.tomushimano.waypoint.di.module.ListenerBinder;
import com.tomushimano.waypoint.di.module.StorageBinder;
import com.tomushimano.waypoint.di.qualifier.DataDir;
import dagger.BindsInstance;
import dagger.Component;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Singleton;
import java.nio.file.Path;

@Singleton
@Component(modules = {
        CommandBinder.class,
        ConfigBinder.class,
        ConfigProvider.class,
        ListenerBinder.class,
        StorageBinder.class
})
public interface WaypointComponent {

    WaypointLoader instance();

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder plugin(final JavaPlugin plugin);

        @BindsInstance
        Builder dataDir(final @DataDir Path dataDir);

        WaypointComponent build();
    }
}
