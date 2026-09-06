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
public final class CommandExceptionHandler_Factory implements Factory<CommandExceptionHandler> {
  private final Provider<Configurable> commandConfigProvider;

  private final Provider<Configurable> langConfigProvider;

  public CommandExceptionHandler_Factory(Provider<Configurable> commandConfigProvider,
      Provider<Configurable> langConfigProvider) {
    this.commandConfigProvider = commandConfigProvider;
    this.langConfigProvider = langConfigProvider;
  }

  @Override
  public CommandExceptionHandler get() {
    return newInstance(commandConfigProvider.get(), langConfigProvider.get());
  }

  public static CommandExceptionHandler_Factory create(Provider<Configurable> commandConfigProvider,
      Provider<Configurable> langConfigProvider) {
    return new CommandExceptionHandler_Factory(commandConfigProvider, langConfigProvider);
  }

  public static CommandExceptionHandler newInstance(Configurable commandConfig,
      Configurable langConfig) {
    return new CommandExceptionHandler(commandConfig, langConfig);
  }
}
