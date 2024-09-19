package com.tomushimano.waypoint.di.module;

import com.tomushimano.waypoint.core.listener.PlayerEventListener;
import com.tomushimano.waypoint.core.listener.WorldEventListener;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;
import org.bukkit.event.Listener;

@Module
public interface ListenerBinder {

    @Binds
    @IntoSet
    Listener bindPlayerEventListener(PlayerEventListener listener);

    @Binds
    @IntoSet
    Listener bindWorldEventListener(WorldEventListener listener);
}