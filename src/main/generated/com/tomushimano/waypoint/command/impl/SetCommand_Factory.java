package com.tomushimano.waypoint.command.impl;

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
public final class SetCommand_Factory implements Factory<SetCommand> {
  private final Provider<WaypointService> waypointServiceProvider;

  private final Provider<Configurable> configProvider;

  public SetCommand_Factory(Provider<WaypointService> waypointServiceProvider,
      Provider<Configurable> configProvider) {
    this.waypointServiceProvider = waypointServiceProvider;
    this.configProvider = configProvider;
  }

  @Override
  public SetCommand get() {
    return newInstance(waypointServiceProvider.get(), configProvider.get());
  }

  public static SetCommand_Factory create(Provider<WaypointService> waypointServiceProvider,
      Provider<Configurable> configProvider) {
    return new SetCommand_Factory(waypointServiceProvider, configProvider);
  }

  public static SetCommand newInstance(WaypointService waypointService, Configurable config) {
    return new SetCommand(waypointService, config);
  }
}
