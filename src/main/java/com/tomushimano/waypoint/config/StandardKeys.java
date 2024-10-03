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
        public static final ConfigKey<String> QUERY_PARAMS = ConfigKey.stringKey("database.mysql.query_params");

        // Connection pool settings
        public static final ConfigKey<Integer> POOL_SIZE = ConfigKey.integerKey("database.mysql.pool.size");
        public static final ConfigKey<Integer> MIN_IDLE = ConfigKey.integerKey("database.mysql.pool.min_idle");
        public static final ConfigKey<Integer> MAX_LIFETIME = ConfigKey.integerKey("database.mysql.pool.max_lifetime");
        public static final ConfigKey<Integer> CONN_TIMEOUT = ConfigKey.integerKey("database.mysql.pool.conn_timeout");

        private Database() {
            throw new DontInvokeMe();
        }
    }

    public static final class Hologram {
        public static final ConfigKey<Double> LINE_PADDING = ConfigKey.doubleKey("hologram.line_padding");
        public static final ConfigKey<Double> TOP_OFFSET = ConfigKey.doubleKey("hologram.top_offset");

        private Hologram() {
            throw new DontInvokeMe();
        }
    }


    private StandardKeys() {
        throw new DontInvokeMe();
    }
}
