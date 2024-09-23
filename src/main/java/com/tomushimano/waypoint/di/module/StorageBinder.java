package com.tomushimano.waypoint.di.module;

import com.tomushimano.waypoint.datastore.Storage;
import com.tomushimano.waypoint.datastore.StorageHolder;
import com.tomushimano.waypoint.datastore.StorageHolderImpl;
import com.tomushimano.waypoint.datastore.StorageKind;
import com.tomushimano.waypoint.datastore.impl.SQLStorage;
import com.tomushimano.waypoint.di.util.EnumKey;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public interface StorageBinder {

    @Binds
    StorageHolder bindStorageHolder(StorageHolderImpl impl);

    @Binds
    @IntoMap
    @EnumKey(StorageKind.SQLITE)
    Storage bindSQLiteStorage(SQLStorage storage);

    @Binds
    @IntoMap
    @EnumKey(StorageKind.MYSQL)
    Storage bindMySQLStorage(SQLStorage storage);
}
