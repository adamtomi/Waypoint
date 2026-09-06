package com.tomushimano.waypoint.command.scaffold;

import com.tomushimano.waypoint.config.Configurable;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata({
    "com.tomushimano.waypoint.di.qualifier.Cmd",
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
public final class ConfirmationHandler_Factory implements Factory<ConfirmationHandler> {
  private final Provider<Configurable> configProvider;

  private final Provider<Configurable> messageConfigProvider;

  public ConfirmationHandler_Factory(Provider<Configurable> configProvider,
      Provider<Configurable> messageConfigProvider) {
    this.configProvider = configProvider;
    this.messageConfigProvider = messageConfigProvider;
  }

  @Override
  public ConfirmationHandler get() {
    return newInstance(configProvider.get(), messageConfigProvider.get());
  }

  public static ConfirmationHandler_Factory create(Provider<Configurable> configProvider,
      Provider<Configurable> messageConfigProvider) {
    return new ConfirmationHandler_Factory(configProvider, messageConfigProvider);
  }

  public static ConfirmationHandler newInstance(Configurable config, Configurable messageConfig) {
    return new ConfirmationHandler(config, messageConfig);
  }
}
