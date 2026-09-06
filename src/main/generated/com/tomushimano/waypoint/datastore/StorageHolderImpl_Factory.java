package com.tomushimano.waypoint.datastore;

import com.tomushimano.waypoint.config.Configurable;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import java.util.Map;
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
public final class StorageHolderImpl_Factory implements Factory<StorageHolderImpl> {
  private final Provider<Configurable> configProvider;

  private final Provider<Map<StorageKind, Storage>> storageImplsProvider;

  public StorageHolderImpl_Factory(Provider<Configurable> configProvider,
      Provider<Map<StorageKind, Storage>> storageImplsProvider) {
    this.configProvider = configProvider;
    this.storageImplsProvider = storageImplsProvider;
  }

  @Override
  public StorageHolderImpl get() {
    return newInstance(configProvider.get(), storageImplsProvider.get());
  }

  public static StorageHolderImpl_Factory create(Provider<Configurable> configProvider,
      Provider<Map<StorageKind, Storage>> storageImplsProvider) {
    return new StorageHolderImpl_Factory(configProvider, storageImplsProvider);
  }

  public static StorageHolderImpl newInstance(Configurable config,
      Map<StorageKind, Storage> storageImpls) {
    return new StorageHolderImpl(config, storageImpls);
  }
}
