package com.tomushimano.waypoint.di.module;

import com.tomushimano.waypoint.command.scaffold.CommandDispatcherFactory;
import com.tomushimano.waypoint.command.scaffold.bukkit.CommandMapAccess;
import dagger.Module;
import dagger.Provides;
import grapefruit.command.dispatcher.CommandDispatcher;
import grapefruit.command.dispatcher.config.DispatcherConfig;
import org.bukkit.command.CommandSender;

import javax.inject.Singleton;

@Module
public class CommandProvider {

    @Provides
    @Singleton
    public CommandDispatcherFactory provideDispatcherFactory(final CommandMapAccess.Factory mapAccessFactory) {
        return commandManager -> CommandDispatcher.using(DispatcherConfig.<CommandSender>builder()
                .registrations(mapAccessFactory.create(commandManager))
                .build());
    }
}
