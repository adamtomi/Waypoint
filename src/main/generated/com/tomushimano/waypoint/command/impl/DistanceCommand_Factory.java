package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.command.scaffold.mapper.WaypointArgumentMapper;
import com.tomushimano.waypoint.config.Configurable;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata({
    "com.tomushimano.waypoint.di.qualifier.Lang",
    "com.tomushimano.waypoint.di.qualifier.Accessible"
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
public final class DistanceCommand_Factory implements Factory<DistanceCommand> {
  private final Provider<Configurable> configProvider;

  private final Provider<WaypointArgumentMapper> waypointMapperProvider;

  public DistanceCommand_Factory(Provider<Configurable> configProvider,
      Provider<WaypointArgumentMapper> waypointMapperProvider) {
    this.configProvider = configProvider;
    this.waypointMapperProvider = waypointMapperProvider;
  }

  @Override
  public DistanceCommand get() {
    return newInstance(configProvider.get(), waypointMapperProvider.get());
  }

  public static DistanceCommand_Factory create(Provider<Configurable> configProvider,
      Provider<WaypointArgumentMapper> waypointMapperProvider) {
    return new DistanceCommand_Factory(configProvider, waypointMapperProvider);
  }

  public static DistanceCommand newInstance(Configurable config,
      WaypointArgumentMapper waypointMapper) {
    return new DistanceCommand(config, waypointMapper);
  }
}
