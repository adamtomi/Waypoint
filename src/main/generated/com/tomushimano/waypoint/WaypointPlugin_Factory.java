package com.tomushimano.waypoint;

import com.tomushimano.waypoint.command.CommandService;
import com.tomushimano.waypoint.config.ConfigHelper;
import com.tomushimano.waypoint.core.navigation.NavigationService;
import com.tomushimano.waypoint.datastore.StorageHolder;
import com.tomushimano.waypoint.util.FutureFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import java.util.Set;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import org.bukkit.event.Listener;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class WaypointPlugin_Factory implements Factory<WaypointPlugin> {
  private final Provider<ConfigHelper> configHelperProvider;

  private final Provider<StorageHolder> storageHolderProvider;

  private final Provider<CommandService> commandServiceProvider;

  private final Provider<NavigationService> navigationServiceProvider;

  private final Provider<FutureFactory> futureFactoryProvider;

  private final Provider<Set<Listener>> listenersProvider;

  public WaypointPlugin_Factory(Provider<ConfigHelper> configHelperProvider,
      Provider<StorageHolder> storageHolderProvider,
      Provider<CommandService> commandServiceProvider,
      Provider<NavigationService> navigationServiceProvider,
      Provider<FutureFactory> futureFactoryProvider, Provider<Set<Listener>> listenersProvider) {
    this.configHelperProvider = configHelperProvider;
    this.storageHolderProvider = storageHolderProvider;
    this.commandServiceProvider = commandServiceProvider;
    this.navigationServiceProvider = navigationServiceProvider;
    this.futureFactoryProvider = futureFactoryProvider;
    this.listenersProvider = listenersProvider;
  }

  @Override
  public WaypointPlugin get() {
    return newInstance(configHelperProvider.get(), storageHolderProvider.get(), commandServiceProvider.get(), navigationServiceProvider.get(), futureFactoryProvider.get(), listenersProvider.get());
  }

  public static WaypointPlugin_Factory create(Provider<ConfigHelper> configHelperProvider,
      Provider<StorageHolder> storageHolderProvider,
      Provider<CommandService> commandServiceProvider,
      Provider<NavigationService> navigationServiceProvider,
      Provider<FutureFactory> futureFactoryProvider, Provider<Set<Listener>> listenersProvider) {
    return new WaypointPlugin_Factory(configHelperProvider, storageHolderProvider, commandServiceProvider, navigationServiceProvider, futureFactoryProvider, listenersProvider);
  }

  public static WaypointPlugin newInstance(ConfigHelper configHelper, StorageHolder storageHolder,
      CommandService commandService, NavigationService navigationService,
      FutureFactory futureFactory, Set<Listener> listeners) {
    return new WaypointPlugin(configHelper, storageHolder, commandService, navigationService, futureFactory, listeners);
  }
}
