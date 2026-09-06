package com.tomushimano.waypoint.datastore.impl;

import com.tomushimano.waypoint.config.Configurable;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import java.nio.file.Path;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata({
    "com.tomushimano.waypoint.di.qualifier.DataDir",
    "com.tomushimano.waypoint.di.qualifier.Cfg"
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
public final class ConnectionFactory_Factory implements Factory<ConnectionFactory> {
  private final Provider<Path> dataFolderProvider;

  private final Provider<Configurable> configProvider;

  public ConnectionFactory_Factory(Provider<Path> dataFolderProvider,
      Provider<Configurable> configProvider) {
    this.dataFolderProvider = dataFolderProvider;
    this.configProvider = configProvider;
  }

  @Override
  public ConnectionFactory get() {
    return newInstance(dataFolderProvider.get(), configProvider.get());
  }

  public static ConnectionFactory_Factory create(Provider<Path> dataFolderProvider,
      Provider<Configurable> configProvider) {
    return new ConnectionFactory_Factory(dataFolderProvider, configProvider);
  }

  public static ConnectionFactory newInstance(Path dataFolder, Configurable config) {
    return new ConnectionFactory(dataFolder, config);
  }
}
