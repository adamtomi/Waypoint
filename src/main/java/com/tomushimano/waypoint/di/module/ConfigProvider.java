package com.tomushimano.waypoint.di.module;

import com.tomushimano.waypoint.config.ConfigHolder;
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
    public ConfigHolder provideCommandYmlHolder(JavaPlugin plugin) {
        return ConfigHolder.fileBacked(plugin.getDataPath().resolve(ConfigHolder.COMMAND_YML));
    }

    @Cfg
    @Provides
    @Singleton
    public ConfigHolder provideConfigYmlHolder(JavaPlugin plugin) {
        return ConfigHolder.fileBacked(plugin.getDataPath().resolve(ConfigHolder.CONFIG_YML));
    }

    @Lang
    @Provides
    @Singleton
    public ConfigHolder provideLangYmlHolder(JavaPlugin plugin) {
        return ConfigHolder.fileBacked(plugin.getDataPath().resolve(ConfigHolder.LANG_YML));
    }
}
