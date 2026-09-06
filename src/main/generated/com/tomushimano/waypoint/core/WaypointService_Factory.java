package com.tomushimano.waypoint.core;

import com.tomushimano.waypoint.datastore.StorageHolder;
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
public final class WaypointService_Factory implements Factory<WaypointService> {
  private final Provider<Waypoint.Factory> waypointFactoryProvider;

  private final Provider<StorageHolder> storageHolderProvider;

  public WaypointService_Factory(Provider<Waypoint.Factory> waypointFactoryProvider,
      Provider<StorageHolder> storageHolderProvider) {
    this.waypointFactoryProvider = waypointFactoryProvider;
    this.storageHolderProvider = storageHolderProvider;
  }

  @Override
  public WaypointService get() {
    return newInstance(waypointFactoryProvider.get(), storageHolderProvider.get());
  }

  public static WaypointService_Factory create(Provider<Waypoint.Factory> waypointFactoryProvider,
      Provider<StorageHolder> storageHolderProvider) {
    return new WaypointService_Factory(waypointFactoryProvider, storageHolderProvider);
  }

  public static WaypointService newInstance(Waypoint.Factory waypointFactory,
      StorageHolder storageHolder) {
    return new WaypointService(waypointFactory, storageHolder);
  }
}
