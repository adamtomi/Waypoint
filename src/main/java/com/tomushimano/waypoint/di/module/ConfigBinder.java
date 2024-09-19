package com.tomushimano.waypoint.di.module;

import com.tomushimano.waypoint.config.ConfigHolder;
import com.tomushimano.waypoint.di.qualifier.Cfg;
import com.tomushimano.waypoint.di.qualifier.Cmd;
import com.tomushimano.waypoint.di.qualifier.Lang;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;

@Module
public interface ConfigBinder {

    @Binds
    @IntoSet
    ConfigHolder bindCommandYml(@Cmd ConfigHolder configHolder);

    @Binds
    @IntoSet
    ConfigHolder bindConfigYml(@Cfg ConfigHolder configHolder);

    @Binds
    @IntoSet
    ConfigHolder bindLangYml(@Lang ConfigHolder configHolder);
}
