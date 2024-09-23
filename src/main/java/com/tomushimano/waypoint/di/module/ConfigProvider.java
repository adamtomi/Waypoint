package com.tomushimano.waypoint.di.module;

import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.di.qualifier.Cfg;
import com.tomushimano.waypoint.di.qualifier.Cmd;
import com.tomushimano.waypoint.di.qualifier.Lang;
import dagger.Module;
import dagger.Provides;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Singleton;

@Module
public class ConfigProvider {

    @Cmd
    @Provides
    @Singleton
    public Configurable provideCommandYmlHolder(JavaPlugin plugin) {
        return Configurable.fileBacked(plugin.getDataPath().resolve(Configurable.COMMAND_YML));
    }

    @Cfg
    @Provides
    @Singleton
    public Configurable provideConfigYmlHolder(JavaPlugin plugin) {
        return Configurable.fileBacked(plugin.getDataPath().resolve(Configurable.CONFIG_YML));
    }

    @Lang
    @Provides
    @Singleton
    public Configurable provideLangYmlHolder(JavaPlugin plugin) {
        return Configurable.fileBacked(plugin.getDataPath().resolve(Configurable.LANG_YML));
    }
}
