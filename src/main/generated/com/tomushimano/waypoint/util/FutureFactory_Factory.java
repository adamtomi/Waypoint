package com.tomushimano.waypoint.util;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class FutureFactory_Factory implements Factory<FutureFactory> {
  @Override
  public FutureFactory get() {
    return newInstance();
  }

  public static FutureFactory_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FutureFactory newInstance() {
    return new FutureFactory();
  }

  private static final class InstanceHolder {
    private static final FutureFactory_Factory INSTANCE = new FutureFactory_Factory();
  }
}
