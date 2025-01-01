package com.tomushimano.waypoint.config;

import com.tomushimano.waypoint.config.parser.ColorParser;
import com.tomushimano.waypoint.datastore.StorageKind;
import com.tomushimano.waypoint.util.DontInvokeMe;
import org.bukkit.Color;

public final class StandardKeys {

    public static final class Database {
        // Basic settings
        public static final ConfigKey<StorageKind> TYPE = ConfigKey.enumKey("database.type", StorageKind.class);

        // SQLite settings
        public static final ConfigKey<String> FILE_NAME = ConfigKey.stringKey("database.sqlite.filename");

        // MySQL settings
        public static final ConfigKey<String> HOST = ConfigKey.stringKey("database.mysql.host");
        public static final ConfigKey<Integer> PORT = ConfigKey.intKey("database.mysql.port");
        public static final ConfigKey<String> USERNAME = ConfigKey.stringKey("database.mysql.username");
        public static final ConfigKey<String> PASSWORD = ConfigKey.stringKey("database.mysql.password");
        public static final ConfigKey<String> DATABASE = ConfigKey.stringKey("database.mysql.database");
        public static final ConfigKey<String> QUERY_PARAMS = ConfigKey.stringKey("database.mysql.query_params");

        // Connection pool settings
        public static final ConfigKey<Integer> POOL_SIZE = ConfigKey.intKey("database.mysql.pool.size");
        public static final ConfigKey<Integer> MIN_IDLE = ConfigKey.intKey("database.mysql.pool.min_idle");
        public static final ConfigKey<Integer> MAX_LIFETIME = ConfigKey.intKey("database.mysql.pool.max_lifetime");
        public static final ConfigKey<Integer> CONN_TIMEOUT = ConfigKey.intKey("database.mysql.pool.conn_timeout");

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

    public static final class Navigation {
        public static final ConfigKey<Integer> ARRIVAL_DISTANCE = ConfigKey.intKey("navigation.arrival_distance");
        public static final ConfigKey<Integer> MIN_REQUIRED_DISTANCE = ConfigKey.intKey("navigation.min_required_distance");
        public static final ConfigKey<Integer> INDICATOR_ARRIVAL_DISTANCE = ConfigKey.intKey("navigation.indicator.arrival_distance");
        public static final ConfigKey<Integer> INDICATOR_DISTANCE_MULTIPLIER = ConfigKey.intKey("navigation.indicator.distance_multiplier");
        public static final ConfigKey<Integer> INDICATOR_MAX_DISTANCE = ConfigKey.intKey("navigation.indicator.max_distance");
        public static final ConfigKey<Color> PARTICLE_COLOR = ConfigKey.strictKey("navigation.particle.color", ColorParser.INSTANCE);
        public static final ConfigKey<Integer> PARTICLE_COUNT = ConfigKey.intKey("navigation.particle.count");
        public static final ConfigKey<Integer> PARTICLE_DENSITY = ConfigKey.intKey("navigation.particle.density");
        public static final ConfigKey<Integer> PARTICLE_Y_OFFSET = ConfigKey.intKey("navigation.particle.y_offset");
        public static final ConfigKey<Float> PARTICLE_SIZE = ConfigKey.floatKey("navigation.particle.size");

        private Navigation() {
            throw new DontInvokeMe();
        }
    }

    private StandardKeys() {
        throw new DontInvokeMe();
    }
}
