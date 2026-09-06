package com.tomushimano.waypoint.di;

import com.google.common.collect.ImmutableSet;
import com.tomushimano.waypoint.WaypointPlugin;
import com.tomushimano.waypoint.command.CommandService;
import com.tomushimano.waypoint.command.CommandService_Factory;
import com.tomushimano.waypoint.command.impl.DistanceCommand;
import com.tomushimano.waypoint.command.impl.DistanceCommand_Factory;
import com.tomushimano.waypoint.command.impl.EditCommand;
import com.tomushimano.waypoint.command.impl.EditCommand_Factory;
import com.tomushimano.waypoint.command.impl.InfoCommand;
import com.tomushimano.waypoint.command.impl.InfoCommand_Factory;
import com.tomushimano.waypoint.command.impl.ListCommand;
import com.tomushimano.waypoint.command.impl.ListCommand_Factory;
import com.tomushimano.waypoint.command.impl.NavigationInfoCommand;
import com.tomushimano.waypoint.command.impl.NavigationInfoCommand_Factory;
import com.tomushimano.waypoint.command.impl.NavigationStartCommand;
import com.tomushimano.waypoint.command.impl.NavigationStartCommand_Factory;
import com.tomushimano.waypoint.command.impl.NavigationStopCommand;
import com.tomushimano.waypoint.command.impl.NavigationStopCommand_Factory;
import com.tomushimano.waypoint.command.impl.ReloadCommand;
import com.tomushimano.waypoint.command.impl.ReloadCommand_Factory;
import com.tomushimano.waypoint.command.impl.RelocateCommand;
import com.tomushimano.waypoint.command.impl.RelocateCommand_Factory;
import com.tomushimano.waypoint.command.impl.RemoveCommand;
import com.tomushimano.waypoint.command.impl.RemoveCommand_Factory;
import com.tomushimano.waypoint.command.impl.SetCommand;
import com.tomushimano.waypoint.command.impl.SetCommand_Factory;
import com.tomushimano.waypoint.command.scaffold.CommandExceptionHandler;
import com.tomushimano.waypoint.command.scaffold.CommandExceptionHandler_Factory;
import com.tomushimano.waypoint.command.scaffold.ConfirmationHandler;
import com.tomushimano.waypoint.command.scaffold.ConfirmationHandler_Factory;
import com.tomushimano.waypoint.command.scaffold.mapper.WaypointArgumentMapper;
import com.tomushimano.waypoint.config.ConfigHelper;
import com.tomushimano.waypoint.config.ConfigHelper_Factory;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.core.LightSourceFactory;
import com.tomushimano.waypoint.core.LightSourceFactory_Factory;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.core.WaypointService;
import com.tomushimano.waypoint.core.WaypointService_Factory;
import com.tomushimano.waypoint.core.Waypoint_Factory_Factory;
import com.tomushimano.waypoint.core.hologram.HologramFactory;
import com.tomushimano.waypoint.core.hologram.HologramFactory_Factory;
import com.tomushimano.waypoint.core.listener.PlayerEventListener;
import com.tomushimano.waypoint.core.navigation.NavigationService;
import com.tomushimano.waypoint.core.navigation.NavigationService_Factory;
import com.tomushimano.waypoint.datastore.Storage;
import com.tomushimano.waypoint.datastore.StorageHolderImpl;
import com.tomushimano.waypoint.datastore.StorageHolderImpl_Factory;
import com.tomushimano.waypoint.datastore.StorageKind;
import com.tomushimano.waypoint.datastore.impl.ConnectionFactory;
import com.tomushimano.waypoint.datastore.impl.ConnectionFactory_Factory;
import com.tomushimano.waypoint.datastore.impl.SQLStorage;
import com.tomushimano.waypoint.datastore.impl.SQLStorage_Factory;
import com.tomushimano.waypoint.di.module.CommandProvider;
import com.tomushimano.waypoint.di.module.CommandProvider_ProvideAccessibleFactory;
import com.tomushimano.waypoint.di.module.CommandProvider_ProvideOwnFactory;
import com.tomushimano.waypoint.di.module.ConfigProvider;
import com.tomushimano.waypoint.di.module.ConfigProvider_ProvideCommandYmlHolderFactory;
import com.tomushimano.waypoint.di.module.ConfigProvider_ProvideConfigYmlHolderFactory;
import com.tomushimano.waypoint.di.module.ConfigProvider_ProvideLangYmlHolderFactory;
import com.tomushimano.waypoint.util.FutureFactory;
import com.tomushimano.waypoint.util.FutureFactory_Factory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.InstanceFactory;
import dagger.internal.MapFactory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.SetFactory;
import grapefruit.command.CommandModule;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

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
public final class DaggerWaypointComponent {
  private DaggerWaypointComponent() {
  }

  public static WaypointComponent.Builder builder() {
    return new Builder();
  }

  private static final class Builder implements WaypointComponent.Builder {
    private Path dataDir;

    @Override
    public Builder dataDir(Path dataDir) {
      this.dataDir = Preconditions.checkNotNull(dataDir);
      return this;
    }

    @Override
    public WaypointComponent build() {
      Preconditions.checkBuilderRequirement(dataDir, Path.class);
      return new WaypointComponentImpl(new CommandProvider(), new ConfigProvider(), dataDir);
    }
  }

  private static final class WaypointComponentImpl implements WaypointComponent {
    private final WaypointComponentImpl waypointComponentImpl = this;

    private Provider<Path> dataDirProvider;

    private Provider<Configurable> provideCommandYmlHolderProvider;

    private Provider<Configurable> provideConfigYmlHolderProvider;

    private Provider<Configurable> provideLangYmlHolderProvider;

    private Provider<Set<Configurable>> setOfConfigurableProvider;

    private Provider<ConfigHelper> configHelperProvider;

    private Provider<ConnectionFactory> connectionFactoryProvider;

    private Provider<HologramFactory> hologramFactoryProvider;

    private Provider<LightSourceFactory> lightSourceFactoryProvider;

    private Provider<Waypoint.Factory> factoryProvider;

    private Provider<FutureFactory> futureFactoryProvider;

    private Provider<SQLStorage> sQLStorageProvider;

    private Provider<Map<StorageKind, Storage>> mapOfStorageKindAndStorageProvider;

    private Provider<StorageHolderImpl> storageHolderImplProvider;

    private Provider<WaypointService> waypointServiceProvider;

    private Provider<WaypointArgumentMapper> provideAccessibleProvider;

    private Provider<DistanceCommand> distanceCommandProvider;

    private Provider<WaypointArgumentMapper> provideOwnProvider;

    private Provider<EditCommand> editCommandProvider;

    private Provider<InfoCommand> infoCommandProvider;

    private Provider<ListCommand> listCommandProvider;

    private Provider<NavigationService> navigationServiceProvider;

    private Provider<NavigationInfoCommand> navigationInfoCommandProvider;

    private Provider<NavigationStartCommand> navigationStartCommandProvider;

    private Provider<NavigationStopCommand> navigationStopCommandProvider;

    private Provider<ReloadCommand> reloadCommandProvider;

    private Provider<RelocateCommand> relocateCommandProvider;

    private Provider<RemoveCommand> removeCommandProvider;

    private Provider<SetCommand> setCommandProvider;

    private Provider<Set<CommandModule<CommandSender>>> setOfCommandModuleOfCommandSenderProvider;

    private Provider<CommandExceptionHandler> commandExceptionHandlerProvider;

    private Provider<ConfirmationHandler> confirmationHandlerProvider;

    private Provider<CommandService> commandServiceProvider;

    private WaypointComponentImpl(CommandProvider commandProviderParam,
        ConfigProvider configProviderParam, Path dataDirParam) {

      initialize(commandProviderParam, configProviderParam, dataDirParam);
      initialize2(commandProviderParam, configProviderParam, dataDirParam);

    }

    private PlayerEventListener playerEventListener() {
      return new PlayerEventListener(waypointServiceProvider.get(), navigationServiceProvider.get());
    }

    private Set<Listener> setOfListener() {
      return ImmutableSet.<Listener>of(playerEventListener());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final CommandProvider commandProviderParam,
        final ConfigProvider configProviderParam, final Path dataDirParam) {
      this.dataDirProvider = InstanceFactory.create(dataDirParam);
      this.provideCommandYmlHolderProvider = DoubleCheck.provider(ConfigProvider_ProvideCommandYmlHolderFactory.create(configProviderParam, dataDirProvider));
      this.provideConfigYmlHolderProvider = DoubleCheck.provider(ConfigProvider_ProvideConfigYmlHolderFactory.create(configProviderParam, dataDirProvider));
      this.provideLangYmlHolderProvider = DoubleCheck.provider(ConfigProvider_ProvideLangYmlHolderFactory.create(configProviderParam, dataDirProvider));
      this.setOfConfigurableProvider = SetFactory.<Configurable>builder(3, 0).addProvider(provideCommandYmlHolderProvider).addProvider(provideConfigYmlHolderProvider).addProvider(provideLangYmlHolderProvider).build();
      this.configHelperProvider = DoubleCheck.provider(ConfigHelper_Factory.create(setOfConfigurableProvider));
      this.connectionFactoryProvider = ConnectionFactory_Factory.create(dataDirProvider, provideConfigYmlHolderProvider);
      this.hologramFactoryProvider = DoubleCheck.provider(HologramFactory_Factory.create(provideConfigYmlHolderProvider, provideLangYmlHolderProvider));
      this.lightSourceFactoryProvider = DoubleCheck.provider(LightSourceFactory_Factory.create(provideConfigYmlHolderProvider));
      this.factoryProvider = DoubleCheck.provider(Waypoint_Factory_Factory.create(hologramFactoryProvider, lightSourceFactoryProvider));
      this.futureFactoryProvider = DoubleCheck.provider(FutureFactory_Factory.create());
      this.sQLStorageProvider = SQLStorage_Factory.create(connectionFactoryProvider, factoryProvider, futureFactoryProvider, provideConfigYmlHolderProvider);
      this.mapOfStorageKindAndStorageProvider = MapFactory.<StorageKind, Storage>builder(2).put(StorageKind.SQLITE, ((Provider) sQLStorageProvider)).put(StorageKind.MYSQL, ((Provider) sQLStorageProvider)).build();
      this.storageHolderImplProvider = DoubleCheck.provider(StorageHolderImpl_Factory.create(provideConfigYmlHolderProvider, mapOfStorageKindAndStorageProvider));
      this.waypointServiceProvider = DoubleCheck.provider(WaypointService_Factory.create(factoryProvider, ((Provider) storageHolderImplProvider)));
      this.provideAccessibleProvider = CommandProvider_ProvideAccessibleFactory.create(commandProviderParam, waypointServiceProvider);
      this.distanceCommandProvider = DistanceCommand_Factory.create(provideLangYmlHolderProvider, provideAccessibleProvider);
      this.provideOwnProvider = CommandProvider_ProvideOwnFactory.create(commandProviderParam, waypointServiceProvider);
      this.editCommandProvider = EditCommand_Factory.create(waypointServiceProvider, provideLangYmlHolderProvider, provideOwnProvider);
      this.infoCommandProvider = InfoCommand_Factory.create(provideLangYmlHolderProvider, provideAccessibleProvider);
      this.listCommandProvider = ListCommand_Factory.create(waypointServiceProvider, provideLangYmlHolderProvider);
      this.navigationServiceProvider = DoubleCheck.provider(NavigationService_Factory.create(provideConfigYmlHolderProvider, provideLangYmlHolderProvider, futureFactoryProvider));
      this.navigationInfoCommandProvider = NavigationInfoCommand_Factory.create(navigationServiceProvider, provideLangYmlHolderProvider);
      this.navigationStartCommandProvider = NavigationStartCommand_Factory.create(navigationServiceProvider, provideAccessibleProvider, provideLangYmlHolderProvider, provideConfigYmlHolderProvider);
      this.navigationStopCommandProvider = NavigationStopCommand_Factory.create(navigationServiceProvider, provideLangYmlHolderProvider);
    }

    @SuppressWarnings("unchecked")
    private void initialize2(final CommandProvider commandProviderParam,
        final ConfigProvider configProviderParam, final Path dataDirParam) {
      this.reloadCommandProvider = ReloadCommand_Factory.create(configHelperProvider, provideLangYmlHolderProvider, waypointServiceProvider);
      this.relocateCommandProvider = RelocateCommand_Factory.create(waypointServiceProvider, provideLangYmlHolderProvider, navigationServiceProvider, provideOwnProvider);
      this.removeCommandProvider = RemoveCommand_Factory.create(waypointServiceProvider, navigationServiceProvider, provideLangYmlHolderProvider, provideOwnProvider);
      this.setCommandProvider = SetCommand_Factory.create(waypointServiceProvider, provideLangYmlHolderProvider);
      this.setOfCommandModuleOfCommandSenderProvider = SetFactory.<CommandModule<CommandSender>>builder(11, 0).addProvider(((Provider) distanceCommandProvider)).addProvider(((Provider) editCommandProvider)).addProvider(((Provider) infoCommandProvider)).addProvider(((Provider) listCommandProvider)).addProvider(((Provider) navigationInfoCommandProvider)).addProvider(((Provider) navigationStartCommandProvider)).addProvider(((Provider) navigationStopCommandProvider)).addProvider(((Provider) reloadCommandProvider)).addProvider(((Provider) relocateCommandProvider)).addProvider(((Provider) removeCommandProvider)).addProvider(((Provider) setCommandProvider)).build();
      this.commandExceptionHandlerProvider = CommandExceptionHandler_Factory.create(provideCommandYmlHolderProvider, provideLangYmlHolderProvider);
      this.confirmationHandlerProvider = ConfirmationHandler_Factory.create(provideCommandYmlHolderProvider, provideLangYmlHolderProvider);
      this.commandServiceProvider = DoubleCheck.provider(CommandService_Factory.create(setOfCommandModuleOfCommandSenderProvider, commandExceptionHandlerProvider, confirmationHandlerProvider, futureFactoryProvider));
    }

    @Override
    public WaypointPlugin plugin() {
      return new WaypointPlugin(configHelperProvider.get(), storageHolderImplProvider.get(), commandServiceProvider.get(), navigationServiceProvider.get(), futureFactoryProvider.get(), setOfListener());
    }
  }
}
