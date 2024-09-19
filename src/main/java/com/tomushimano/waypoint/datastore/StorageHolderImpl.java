package com.tomushimano.waypoint.datastore;

import com.tomushimano.waypoint.config.ConfigHolder;
import com.tomushimano.waypoint.config.StandardKeys;
import com.tomushimano.waypoint.datastore.impl.ConnectionFactory;
import com.tomushimano.waypoint.datastore.impl.SQLStorage;
import com.tomushimano.waypoint.di.qualifier.Cfg;
import com.tomushimano.waypoint.util.Memoized;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class StorageHolderImpl implements StorageHolder {
    private final Memoized<Storage> implHolder = Memoized.of(this::compute);
    private final JavaPlugin plugin;
    private final ConfigHolder config;

    @Inject
    public StorageHolderImpl(JavaPlugin plugin, @Cfg ConfigHolder config) {
        this.config = config;
        this.plugin = plugin;
    }

    @Override
    public Storage get() {
        return this.implHolder.get();
    }

    private Storage compute() {
        StorageKind storageKind = this.config.get(StandardKeys.Database.TYPE);
        return switch (storageKind) {
            case SQLITE, MYSQL -> new SQLStorage(new ConnectionFactory(this.plugin, this.config, storageKind.equals(StorageKind.SQLITE)));
        };
    }
}
