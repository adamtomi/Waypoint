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
public final class RelocateCommand_Factory implements Factory<RelocateCommand> {
  private final Provider<WaypointService> waypointServiceProvider;

  private final Provider<Configurable> configProvider;

  private final Provider<NavigationService> navigationServiceProvider;

  private final Provider<WaypointArgumentMapper> waypointMapperProvider;

  public RelocateCommand_Factory(Provider<WaypointService> waypointServiceProvider,
      Provider<Configurable> configProvider, Provider<NavigationService> navigationServiceProvider,
      Provider<WaypointArgumentMapper> waypointMapperProvider) {
    this.waypointServiceProvider = waypointServiceProvider;
    this.configProvider = configProvider;
    this.navigationServiceProvider = navigationServiceProvider;
    this.waypointMapperProvider = waypointMapperProvider;
  }

  @Override
  public RelocateCommand get() {
    return newInstance(waypointServiceProvider.get(), configProvider.get(), navigationServiceProvider.get(), waypointMapperProvider.get());
  }

  public static RelocateCommand_Factory create(Provider<WaypointService> waypointServiceProvider,
      Provider<Configurable> configProvider, Provider<NavigationService> navigationServiceProvider,
      Provider<WaypointArgumentMapper> waypointMapperProvider) {
    return new RelocateCommand_Factory(waypointServiceProvider, configProvider, navigationServiceProvider, waypointMapperProvider);
  }

  public static RelocateCommand newInstance(WaypointService waypointService, Configurable config,
      NavigationService navigationService, WaypointArgumentMapper waypointMapper) {
    return new RelocateCommand(waypointService, config, navigationService, waypointMapper);
  }
}
