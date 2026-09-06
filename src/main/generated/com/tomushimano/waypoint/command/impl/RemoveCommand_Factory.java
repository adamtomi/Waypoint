package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.command.scaffold.mapper.WaypointArgumentMapper;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.core.WaypointService;
import com.tomushimano.waypoint.core.navigation.NavigationService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata({
    "com.tomushimano.waypoint.di.qualifier.Lang",
    "com.tomushimano.waypoint.di.qualifier.Own"
})
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
public final class RemoveCommand_Factory implements Factory<RemoveCommand> {
  private final Provider<WaypointService> waypointServiceProvider;

  private final Provider<NavigationService> navigationServiceProvider;

  private final Provider<Configurable> configProvider;

  private final Provider<WaypointArgumentMapper> waypointMapperProvider;

  public RemoveCommand_Factory(Provider<WaypointService> waypointServiceProvider,
      Provider<NavigationService> navigationServiceProvider, Provider<Configurable> configProvider,
      Provider<WaypointArgumentMapper> waypointMapperProvider) {
    this.waypointServiceProvider = waypointServiceProvider;
    this.navigationServiceProvider = navigationServiceProvider;
    this.configProvider = configProvider;
    this.waypointMapperProvider = waypointMapperProvider;
  }

  @Override
  public RemoveCommand get() {
    return newInstance(waypointServiceProvider.get(), navigationServiceProvider.get(), configProvider.get(), waypointMapperProvider.get());
  }

  public static RemoveCommand_Factory create(Provider<WaypointService> waypointServiceProvider,
      Provider<NavigationService> navigationServiceProvider, Provider<Configurable> configProvider,
      Provider<WaypointArgumentMapper> waypointMapperProvider) {
    return new RemoveCommand_Factory(waypointServiceProvider, navigationServiceProvider, configProvider, waypointMapperProvider);
  }

  public static RemoveCommand newInstance(WaypointService waypointService,
      NavigationService navigationService, Configurable config,
      WaypointArgumentMapper waypointMapper) {
    return new RemoveCommand(waypointService, navigationService, config, waypointMapper);
  }
}
