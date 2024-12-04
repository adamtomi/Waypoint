package com.tomushimano.waypoint.di.module;

import com.tomushimano.waypoint.command.scaffold.bukkit.CommandMapAccess;
import dagger.Module;
import dagger.Provides;
import grapefruit.command.dispatcher.CommandAuthorizer;
import grapefruit.command.dispatcher.CommandDispatcher;
import grapefruit.command.dispatcher.config.DispatcherConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

@Module
public class CommandProvider {

    @Provides
    public CommandAuthorizer<CommandSender> provideCommandAuthorizer() {
        return Permissible::hasPermission;
    }

    @Provides
    public DispatcherConfig<CommandSender> provideDispatcherConfig(
            final CommandAuthorizer<CommandSender> authorizer,
            final CommandMapAccess mapAccess
    ) {
        return DispatcherConfig.<CommandSender>builder()
                .authorize(authorizer)
                .registrations(mapAccess)
                .build();
    }

    @Provides
    public CommandDispatcher<CommandSender> provideCommandDispatcher(final DispatcherConfig<CommandSender> config) {
        return CommandDispatcher.using(config);
    }
}
