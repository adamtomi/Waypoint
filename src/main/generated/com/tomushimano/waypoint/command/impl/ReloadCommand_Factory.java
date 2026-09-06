package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.config.ConfigHelper;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.core.WaypointService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("com.tomushimano.waypoint.di.qualifier.Lang")
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
public final class ReloadCommand_Factory implements Factory<ReloadCommand> {
  private final Provider<ConfigHelper> configHelperProvider;

  private final Provider<Configurable> configProvider;

  private final Provider<WaypointService> waypointServiceProvider;

  public ReloadCommand_Factory(Provider<ConfigHelper> configHelperProvider,
      Provider<Configurable> configProvider, Provider<WaypointService> waypointServiceProvider) {
    this.configHelperProvider = configHelperProvider;
    this.configProvider = configProvider;
    this.waypointServiceProvider = waypointServiceProvider;
  }

  @Override
  public ReloadCommand get() {
    return newInstance(configHelperProvider.get(), configProvider.get(), waypointServiceProvider.get());
  }

  public static ReloadCommand_Factory create(Provider<ConfigHelper> configHelperProvider,
      Provider<Configurable> configProvider, Provider<WaypointService> waypointServiceProvider) {
    return new ReloadCommand_Factory(configHelperProvider, configProvider, waypointServiceProvider);
  }

  public static ReloadCommand newInstance(ConfigHelper configHelper, Configurable config,
      WaypointService waypointService) {
    return new ReloadCommand(configHelper, config, waypointService);
  }
}
