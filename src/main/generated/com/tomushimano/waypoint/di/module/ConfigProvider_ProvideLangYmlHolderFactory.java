package com.tomushimano.waypoint.di.module;

import com.tomushimano.waypoint.config.Configurable;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import java.nio.file.Path;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata({
    "com.tomushimano.waypoint.di.qualifier.Lang",
    "com.tomushimano.waypoint.di.qualifier.DataDir"
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
public final class ConfigProvider_ProvideLangYmlHolderFactory implements Factory<Configurable> {
  private final ConfigProvider module;

  private final Provider<Path> dataFolderProvider;

  public ConfigProvider_ProvideLangYmlHolderFactory(ConfigProvider module,
      Provider<Path> dataFolderProvider) {
    this.module = module;
    this.dataFolderProvider = dataFolderProvider;
  }

  @Override
  public Configurable get() {
    return provideLangYmlHolder(module, dataFolderProvider.get());
  }

  public static ConfigProvider_ProvideLangYmlHolderFactory create(ConfigProvider module,
      Provider<Path> dataFolderProvider) {
    return new ConfigProvider_ProvideLangYmlHolderFactory(module, dataFolderProvider);
  }

  public static Configurable provideLangYmlHolder(ConfigProvider instance, Path dataFolder) {
    return Preconditions.checkNotNullFromProvides(instance.provideLangYmlHolder(dataFolder));
  }
}
