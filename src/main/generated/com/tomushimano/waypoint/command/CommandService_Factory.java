package com.tomushimano.waypoint.command;

import com.tomushimano.waypoint.command.scaffold.CommandExceptionHandler;
import com.tomushimano.waypoint.command.scaffold.ConfirmationHandler;
import com.tomushimano.waypoint.util.FutureFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import grapefruit.command.CommandModule;
import java.util.Set;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import org.bukkit.command.CommandSender;

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
public final class CommandService_Factory implements Factory<CommandService> {
  private final Provider<Set<CommandModule<CommandSender>>> commandsProvider;

  private final Provider<CommandExceptionHandler> exceptionHandlerProvider;

  private final Provider<ConfirmationHandler> confirmationHandlerProvider;

  private final Provider<FutureFactory> futureFactoryProvider;

  public CommandService_Factory(Provider<Set<CommandModule<CommandSender>>> commandsProvider,
      Provider<CommandExceptionHandler> exceptionHandlerProvider,
      Provider<ConfirmationHandler> confirmationHandlerProvider,
      Provider<FutureFactory> futureFactoryProvider) {
    this.commandsProvider = commandsProvider;
    this.exceptionHandlerProvider = exceptionHandlerProvider;
    this.confirmationHandlerProvider = confirmationHandlerProvider;
    this.futureFactoryProvider = futureFactoryProvider;
  }

  @Override
  public CommandService get() {
    return newInstance(commandsProvider.get(), exceptionHandlerProvider.get(), confirmationHandlerProvider.get(), futureFactoryProvider.get());
  }

  public static CommandService_Factory create(
      Provider<Set<CommandModule<CommandSender>>> commandsProvider,
      Provider<CommandExceptionHandler> exceptionHandlerProvider,
      Provider<ConfirmationHandler> confirmationHandlerProvider,
      Provider<FutureFactory> futureFactoryProvider) {
    return new CommandService_Factory(commandsProvider, exceptionHandlerProvider, confirmationHandlerProvider, futureFactoryProvider);
  }

  public static CommandService newInstance(Set<CommandModule<CommandSender>> commands,
      CommandExceptionHandler exceptionHandler, ConfirmationHandler confirmationHandler,
      FutureFactory futureFactory) {
    return new CommandService(commands, exceptionHandler, confirmationHandler, futureFactory);
  }
}
