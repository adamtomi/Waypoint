package com.tomushimano.waypoint.core.listener;

import com.tomushimano.waypoint.core.WaypointService;
import com.tomushimano.waypoint.core.navigation.NavigationService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class PlayerEventListener_Factory implements Factory<PlayerEventListener> {
  private final Provider<WaypointService> waypointServiceProvider;

  private final Provider<NavigationService> navigationServiceProvider;

  public PlayerEventListener_Factory(Provider<WaypointService> waypointServiceProvider,
      Provider<NavigationService> navigationServiceProvider) {
    this.waypointServiceProvider = waypointServiceProvider;
    this.navigationServiceProvider = navigationServiceProvider;
  }

  @Override
  public PlayerEventListener get() {
    return newInstance(waypointServiceProvider.get(), navigationServiceProvider.get());
  }

  public static PlayerEventListener_Factory create(
      Provider<WaypointService> waypointServiceProvider,
      Provider<NavigationService> navigationServiceProvider) {
    return new PlayerEventListener_Factory(waypointServiceProvider, navigationServiceProvider);
  }

  public static PlayerEventListener newInstance(WaypointService waypointService,
      NavigationService navigationService) {
    return new PlayerEventListener(waypointService, navigationService);
  }
}
