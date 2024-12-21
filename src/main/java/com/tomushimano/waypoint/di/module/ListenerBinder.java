package com.tomushimano.waypoint.di.module;

import com.tomushimano.waypoint.core.listener.PlayerEventListener;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;
import org.bukkit.event.Listener;

@Module
public interface ListenerBinder {

    @Binds
    @IntoSet
    Listener bindPlayerEventListener(final PlayerEventListener listener);
}
