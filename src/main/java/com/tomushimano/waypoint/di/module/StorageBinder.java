package com.tomushimano.waypoint.di.module;

import com.tomushimano.waypoint.datastore.Storage;
import com.tomushimano.waypoint.datastore.StorageHolder;
import com.tomushimano.waypoint.datastore.StorageHolderImpl;
import com.tomushimano.waypoint.datastore.StorageKind;
import com.tomushimano.waypoint.datastore.impl.SQLStorage;
import com.tomushimano.waypoint.di.util.StorageKindKey;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public interface StorageBinder {

    @Binds
    StorageHolder bindStorageHolder(final StorageHolderImpl impl);

    @Binds
    @IntoMap
    @StorageKindKey(StorageKind.SQLITE)
    Storage bindSQLiteStorage(final SQLStorage storage);

    @Binds
    @IntoMap
    @StorageKindKey(StorageKind.MYSQL)
    Storage bindMySQLStorage(final SQLStorage storage);
}
