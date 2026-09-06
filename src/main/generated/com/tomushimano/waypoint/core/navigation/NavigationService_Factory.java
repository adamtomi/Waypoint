package com.tomushimano.waypoint.core.navigation;

import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.util.FutureFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata({
    "com.tomushimano.waypoint.di.qualifier.Cfg",
    "com.tomushimano.waypoint.di.qualifier.Lang"
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
public final class NavigationService_Factory implements Factory<NavigationService> {
  private final Provider<Configurable> configProvider;

  private final Provider<Configurable> langConfigProvider;

  private final Provider<FutureFactory> futureFactoryProvider;

  public NavigationService_Factory(Provider<Configurable> configProvider,
      Provider<Configurable> langConfigProvider, Provider<FutureFactory> futureFactoryProvider) {
    this.configProvider = configProvider;
    this.langConfigProvider = langConfigProvider;
    this.futureFactoryProvider = futureFactoryProvider;
  }

  @Override
  public NavigationService get() {
    return newInstance(configProvider.get(), langConfigProvider.get(), futureFactoryProvider.get());
  }

  public static NavigationService_Factory create(Provider<Configurable> configProvider,
      Provider<Configurable> langConfigProvider, Provider<FutureFactory> futureFactoryProvider) {
    return new NavigationService_Factory(configProvider, langConfigProvider, futureFactoryProvider);
  }

  public static NavigationService newInstance(Configurable config, Configurable langConfig,
      FutureFactory futureFactory) {
    return new NavigationService(config, langConfig, futureFactory);
  }
}
