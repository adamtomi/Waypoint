package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.command.scaffold.mapper.WaypointArgumentMapper;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.core.navigation.NavigationService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata({
    "com.tomushimano.waypoint.di.qualifier.Accessible",
    "com.tomushimano.waypoint.di.qualifier.Lang",
    "com.tomushimano.waypoint.di.qualifier.Cfg"
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
public final class NavigationStartCommand_Factory implements Factory<NavigationStartCommand> {
  private final Provider<NavigationService> navigationServiceProvider;

  private final Provider<WaypointArgumentMapper> waypointMapperProvider;

  private final Provider<Configurable> langConfigProvider;

  private final Provider<Configurable> configProvider;

  public NavigationStartCommand_Factory(Provider<NavigationService> navigationServiceProvider,
      Provider<WaypointArgumentMapper> waypointMapperProvider,
      Provider<Configurable> langConfigProvider, Provider<Configurable> configProvider) {
    this.navigationServiceProvider = navigationServiceProvider;
    this.waypointMapperProvider = waypointMapperProvider;
    this.langConfigProvider = langConfigProvider;
    this.configProvider = configProvider;
  }

  @Override
  public NavigationStartCommand get() {
    return newInstance(navigationServiceProvider.get(), waypointMapperProvider.get(), langConfigProvider.get(), configProvider.get());
  }

  public static NavigationStartCommand_Factory create(
      Provider<NavigationService> navigationServiceProvider,
      Provider<WaypointArgumentMapper> waypointMapperProvider,
      Provider<Configurable> langConfigProvider, Provider<Configurable> configProvider) {
    return new NavigationStartCommand_Factory(navigationServiceProvider, waypointMapperProvider, langConfigProvider, configProvider);
  }

  public static NavigationStartCommand newInstance(NavigationService navigationService,
      WaypointArgumentMapper waypointMapper, Configurable langConfig, Configurable config) {
    return new NavigationStartCommand(navigationService, waypointMapper, langConfig, config);
  }
}
