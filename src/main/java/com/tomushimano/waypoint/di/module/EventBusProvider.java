package com.tomushimano.waypoint.di.module;

import com.google.common.eventbus.EventBus;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Deprecated
@Module
public class EventBusProvider {

    @Provides
    @Singleton
    public EventBus provideEventBus() {
        return new EventBus();
    }
}
