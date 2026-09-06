package com.tomushimano.waypoint.datastore.impl;

import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.util.FutureFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class SQLStorage_Factory implements Factory<SQLStorage> {
  private final Provider<ConnectionFactory> connectionFactoryProvider;

  private final Provider<Waypoint.Factory> waypointFactoryProvider;

  private final Provider<FutureFactory> futureFactoryProvider;

  private final Provider<Configurable> configProvider;

  public SQLStorage_Factory(Provider<ConnectionFactory> connectionFactoryProvider,
      Provider<Waypoint.Factory> waypointFactoryProvider,
      Provider<FutureFactory> futureFactoryProvider, Provider<Configurable> configProvider) {
    this.connectionFactoryProvider = connectionFactoryProvider;
    this.waypointFactoryProvider = waypointFactoryProvider;
    this.futureFactoryProvider = futureFactoryProvider;
    this.configProvider = configProvider;
  }

  @Override
  public SQLStorage get() {
    return newInstance(connectionFactoryProvider.get(), waypointFactoryProvider.get(), futureFactoryProvider.get(), configProvider.get());
  }

  public static SQLStorage_Factory create(Provider<ConnectionFactory> connectionFactoryProvider,
      Provider<Waypoint.Factory> waypointFactoryProvider,
      Provider<FutureFactory> futureFactoryProvider, Provider<Configurable> configProvider) {
    return new SQLStorage_Factory(connectionFactoryProvider, waypointFactoryProvider, futureFactoryProvider, configProvider);
  }

  public static SQLStorage newInstance(ConnectionFactory connectionFactory,
      Waypoint.Factory waypointFactory, FutureFactory futureFactory, Configurable config) {
    return new SQLStorage(connectionFactory, waypointFactory, futureFactory, config);
  }
}
