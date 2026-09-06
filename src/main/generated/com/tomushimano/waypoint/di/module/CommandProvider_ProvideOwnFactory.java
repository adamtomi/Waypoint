package com.tomushimano.waypoint.di.module;

import com.tomushimano.waypoint.command.scaffold.mapper.WaypointArgumentMapper;
import com.tomushimano.waypoint.core.WaypointService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("com.tomushimano.waypoint.di.qualifier.Own")
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
public final class CommandProvider_ProvideOwnFactory implements Factory<WaypointArgumentMapper> {
  private final CommandProvider module;

  private final Provider<WaypointService> serviceProvider;

  public CommandProvider_ProvideOwnFactory(CommandProvider module,
      Provider<WaypointService> serviceProvider) {
    this.module = module;
    this.serviceProvider = serviceProvider;
  }

  @Override
  public WaypointArgumentMapper get() {
    return provideOwn(module, serviceProvider.get());
  }

  public static CommandProvider_ProvideOwnFactory create(CommandProvider module,
      Provider<WaypointService> serviceProvider) {
    return new CommandProvider_ProvideOwnFactory(module, serviceProvider);
  }

  public static WaypointArgumentMapper provideOwn(CommandProvider instance,
      WaypointService service) {
    return Preconditions.checkNotNullFromProvides(instance.provideOwn(service));
  }
}
