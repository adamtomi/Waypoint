package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.core.navigation.NavigationService;
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
public final class NavigationStopCommand_Factory implements Factory<NavigationStopCommand> {
  private final Provider<NavigationService> navigationServiceProvider;

  private final Provider<Configurable> configProvider;

  public NavigationStopCommand_Factory(Provider<NavigationService> navigationServiceProvider,
      Provider<Configurable> configProvider) {
    this.navigationServiceProvider = navigationServiceProvider;
    this.configProvider = configProvider;
  }

  @Override
  public NavigationStopCommand get() {
    return newInstance(navigationServiceProvider.get(), configProvider.get());
  }

  public static NavigationStopCommand_Factory create(
      Provider<NavigationService> navigationServiceProvider,
      Provider<Configurable> configProvider) {
    return new NavigationStopCommand_Factory(navigationServiceProvider, configProvider);
  }

  public static NavigationStopCommand newInstance(NavigationService navigationService,
      Configurable config) {
    return new NavigationStopCommand(navigationService, config);
  }
}
