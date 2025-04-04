package com.tomushimano.waypoint.di;

import com.tomushimano.waypoint.command.CommandService;
import com.tomushimano.waypoint.config.ConfigHelper;
import com.tomushimano.waypoint.core.navigation.NavigationService;
import com.tomushimano.waypoint.datastore.StorageHolder;
import com.tomushimano.waypoint.di.module.CommandBinder;
import com.tomushimano.waypoint.di.module.ConfigBinder;
import com.tomushimano.waypoint.di.module.ConfigProvider;
import com.tomushimano.waypoint.di.module.ListenerBinder;
import com.tomushimano.waypoint.di.module.StorageBinder;
import com.tomushimano.waypoint.di.qualifier.DataDir;
import dagger.BindsInstance;
import dagger.Component;
import org.bukkit.event.Listener;

import javax.inject.Singleton;
import java.nio.file.Path;
import java.util.Set;

@Singleton
@Component(modules = {
        CommandBinder.class,
        ConfigBinder.class,
        ConfigProvider.class,
        ListenerBinder.class,
        StorageBinder.class
})
public interface WaypointComponent {

    CommandService commandService();

    Set<Listener> listeners();

    ConfigHelper configHelper();

    StorageHolder storageHolder();

    NavigationService navigationService();

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder dataDir(final @DataDir Path dataDir);

        WaypointComponent build();
    }
}
