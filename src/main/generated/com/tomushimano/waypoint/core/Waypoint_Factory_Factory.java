package com.tomushimano.waypoint.core;

import com.tomushimano.waypoint.core.hologram.HologramFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
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
public final class Waypoint_Factory_Factory implements Factory<Waypoint.Factory> {
  private final Provider<HologramFactory> hologramFactoryProvider;

  private final Provider<LightSourceFactory> lightSourceFactoryProvider;

  public Waypoint_Factory_Factory(Provider<HologramFactory> hologramFactoryProvider,
      Provider<LightSourceFactory> lightSourceFactoryProvider) {
    this.hologramFactoryProvider = hologramFactoryProvider;
    this.lightSourceFactoryProvider = lightSourceFactoryProvider;
  }

  @Override
  public Waypoint.Factory get() {
    return newInstance(hologramFactoryProvider.get(), lightSourceFactoryProvider.get());
  }

  public static Waypoint_Factory_Factory create(Provider<HologramFactory> hologramFactoryProvider,
      Provider<LightSourceFactory> lightSourceFactoryProvider) {
    return new Waypoint_Factory_Factory(hologramFactoryProvider, lightSourceFactoryProvider);
  }

  public static Waypoint.Factory newInstance(HologramFactory hologramFactory,
      LightSourceFactory lightSourceFactory) {
    return new Waypoint.Factory(hologramFactory, lightSourceFactory);
  }
}
