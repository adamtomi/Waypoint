package com.tomushimano.waypoint.di.module;

import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.di.qualifier.Cfg;
import com.tomushimano.waypoint.di.qualifier.Cmd;
import com.tomushimano.waypoint.di.qualifier.DataDir;
import com.tomushimano.waypoint.di.qualifier.Lang;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.nio.file.Path;

@Module
public class ConfigProvider {

    @Cmd
    @Provides
    @Singleton
    public Configurable provideCommandYmlHolder(final @DataDir Path dataFolder) {
        return Configurable.fileBacked(dataFolder.resolve(Configurable.COMMAND_YML));
    }

    @Cfg
    @Provides
    @Singleton
    public Configurable provideConfigYmlHolder(final @DataDir Path dataFolder) {
        return Configurable.fileBacked(dataFolder.resolve(Configurable.CONFIG_YML));
    }

    @Lang
    @Provides
    @Singleton
    public Configurable provideLangYmlHolder(final @DataDir Path dataFolder) {
        return Configurable.fileBacked(dataFolder.resolve(Configurable.LANG_YML));
    }
}
