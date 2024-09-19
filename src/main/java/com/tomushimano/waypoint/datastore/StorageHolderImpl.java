package com.tomushimano.waypoint.datastore;

import com.tomushimano.waypoint.config.ConfigHolder;
import com.tomushimano.waypoint.config.StandardKeys;
import com.tomushimano.waypoint.datastore.impl.ConnectionFactory;
import com.tomushimano.waypoint.datastore.impl.SQLStorage;
import com.tomushimano.waypoint.di.qualifier.Cfg;
import com.tomushimano.waypoint.util.Memoized;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class StorageHolderImpl implements StorageHolder {
    private final Memoized<Storage> implHolder = Memoized.of(this::compute);
    private final ConfigHolder config;

    @Inject
    public StorageHolderImpl(@Cfg ConfigHolder config) {
        this.config = config;
    }

    @Override
    public Storage get() {
        return this.implHolder.get();
    }

    private Storage compute() {
        StorageKind storageKind = this.config.get(StandardKeys.Database.TYPE);
        return switch (storageKind) {
            case SQLITE, MYSQL -> new SQLStorage(new ConnectionFactory());
        };
    }
}
