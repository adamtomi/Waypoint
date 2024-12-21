package com.tomushimano.waypoint.di.module;

import com.tomushimano.waypoint.config.Configurable;
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
    Configurable bindCommandYml(final @Cmd Configurable configurable);

    @Binds
    @IntoSet
    Configurable bindConfigYml(final @Cfg Configurable configurable);

    @Binds
    @IntoSet
    Configurable bindLangYml(final @Lang Configurable configurable);
}
