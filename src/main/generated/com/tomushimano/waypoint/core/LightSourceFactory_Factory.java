package com.tomushimano.waypoint.core;

import com.tomushimano.waypoint.config.Configurable;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("com.tomushimano.waypoint.di.qualifier.Cfg")
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
public final class LightSourceFactory_Factory implements Factory<LightSourceFactory> {
  private final Provider<Configurable> configProvider;

  public LightSourceFactory_Factory(Provider<Configurable> configProvider) {
    this.configProvider = configProvider;
  }

  @Override
  public LightSourceFactory get() {
    return newInstance(configProvider.get());
  }

  public static LightSourceFactory_Factory create(Provider<Configurable> configProvider) {
    return new LightSourceFactory_Factory(configProvider);
  }

  public static LightSourceFactory newInstance(Configurable config) {
    return new LightSourceFactory(config);
  }
}
