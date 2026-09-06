package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.command.scaffold.mapper.WaypointArgumentMapper;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.core.WaypointService;
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
public final class EditCommand_Factory implements Factory<EditCommand> {
  private final Provider<WaypointService> waypointServiceProvider;

  private final Provider<Configurable> configProvider;

  private final Provider<WaypointArgumentMapper> waypointMapperProvider;

  public EditCommand_Factory(Provider<WaypointService> waypointServiceProvider,
      Provider<Configurable> configProvider,
      Provider<WaypointArgumentMapper> waypointMapperProvider) {
    this.waypointServiceProvider = waypointServiceProvider;
    this.configProvider = configProvider;
    this.waypointMapperProvider = waypointMapperProvider;
  }

  @Override
  public EditCommand get() {
    return newInstance(waypointServiceProvider.get(), configProvider.get(), waypointMapperProvider.get());
  }

  public static EditCommand_Factory create(Provider<WaypointService> waypointServiceProvider,
      Provider<Configurable> configProvider,
      Provider<WaypointArgumentMapper> waypointMapperProvider) {
    return new EditCommand_Factory(waypointServiceProvider, configProvider, waypointMapperProvider);
  }

  public static EditCommand newInstance(WaypointService waypointService, Configurable config,
      WaypointArgumentMapper waypointMapper) {
    return new EditCommand(waypointService, config, waypointMapper);
  }
}
