package com.tomushimano.waypoint.config;

import com.tomushimano.waypoint.datastore.StorageKind;
import com.tomushimano.waypoint.util.DontInvokeMe;

public final class StandardKeys {

    public static final class Database {
        // Basic settings
        public static final ConfigKey<StorageKind> TYPE = ConfigKey.enumKey("database.type", StorageKind.class);

        // SQLite settings
        public static final ConfigKey<String> FILE_NAME = ConfigKey.stringKey("database.sqlite.filename");

        // MySQL settings
        public static final ConfigKey<String> HOST = ConfigKey.stringKey("database.mysql.host");
        public static final ConfigKey<Integer> PORT = ConfigKey.integerKey("database.mysql.port");
        public static final ConfigKey<String> USERNAME = ConfigKey.stringKey("database.mysql.username");
        public static final ConfigKey<String> PASSWORD = ConfigKey.stringKey("database.mysql.password");
        public static final ConfigKey<String> DATABASE = ConfigKey.stringKey("database.mysql.database");

        private Database() {}
    }


    private StandardKeys() {
        throw new DontInvokeMe();
    }
}
