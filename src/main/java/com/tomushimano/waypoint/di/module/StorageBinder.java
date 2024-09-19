package com.tomushimano.waypoint.di.module;

import com.tomushimano.waypoint.datastore.StorageHolder;
import com.tomushimano.waypoint.datastore.StorageHolderImpl;
import dagger.Binds;
import dagger.Module;

@Module
public interface StorageBinder {

    @Binds
    StorageHolder bindStorageHolder(StorageHolderImpl impl);
}
