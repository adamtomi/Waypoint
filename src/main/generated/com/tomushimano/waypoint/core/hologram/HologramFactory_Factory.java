package com.tomushimano.waypoint.core.hologram;

import com.tomushimano.waypoint.config.Configurable;
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
public final class HologramFactory_Factory implements Factory<HologramFactory> {
  private final Provider<Configurable> configProvider;

  private final Provider<Configurable> langConfigProvider;

  public HologramFactory_Factory(Provider<Configurable> configProvider,
      Provider<Configurable> langConfigProvider) {
    this.configProvider = configProvider;
    this.langConfigProvider = langConfigProvider;
  }

  @Override
  public HologramFactory get() {
    return newInstance(configProvider.get(), langConfigProvider.get());
  }

  public static HologramFactory_Factory create(Provider<Configurable> configProvider,
      Provider<Configurable> langConfigProvider) {
    return new HologramFactory_Factory(configProvider, langConfigProvider);
  }

  public static HologramFactory newInstance(Configurable config, Configurable langConfig) {
    return new HologramFactory(config, langConfig);
  }
}
