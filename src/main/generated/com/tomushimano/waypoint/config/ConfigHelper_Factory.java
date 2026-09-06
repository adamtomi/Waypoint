package com.tomushimano.waypoint.config;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import java.util.Set;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class ConfigHelper_Factory implements Factory<ConfigHelper> {
  private final Provider<Set<Configurable>> configurationsProvider;

  public ConfigHelper_Factory(Provider<Set<Configurable>> configurationsProvider) {
    this.configurationsProvider = configurationsProvider;
  }

  @Override
  public ConfigHelper get() {
    return newInstance(configurationsProvider.get());
  }

  public static ConfigHelper_Factory create(Provider<Set<Configurable>> configurationsProvider) {
    return new ConfigHelper_Factory(configurationsProvider);
  }

  public static ConfigHelper newInstance(Set<Configurable> configurations) {
    return new ConfigHelper(configurations);
  }
}
