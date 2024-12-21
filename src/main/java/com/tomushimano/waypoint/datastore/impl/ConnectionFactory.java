package com.tomushimano.waypoint.datastore.impl;

import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.config.StandardKeys;
import com.tomushimano.waypoint.datastore.StorageKind;
import com.tomushimano.waypoint.di.qualifier.Cfg;
import com.tomushimano.waypoint.util.Memoized;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Strings.isNullOrEmpty;

public class ConnectionFactory implements AutoCloseable {
    private final Memoized<HikariDataSource> dataSource = Memoized.of(this::createDataSource);
    private final JavaPlugin plugin;
    private final Configurable config;

    @Inject
    public ConnectionFactory(final JavaPlugin plugin, final @Cfg Configurable config) {
        this.plugin = plugin;
        this.config = config;
    }

    private HikariDataSource createDataSource() {
        // Create config containing common settings
        final HikariConfig config = new HikariConfig();
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setPoolName("WaypointPool");

        if (this.config.get(StandardKeys.Database.TYPE).equals(StorageKind.SQLITE)) {
            config.setDriverClassName("org.sqlite.JDBC");
            config.setJdbcUrl("jdbc:sqlite:%s".formatted(this.plugin.getDataPath().resolve(this.config.get(StandardKeys.Database.FILE_NAME))));
        } else {
            config.setDriverClassName("com.mysql.jdbc.Driver");
            config.setJdbcUrl(createJdbcUrl(
                    this.config.get(StandardKeys.Database.HOST),
                    this.config.get(StandardKeys.Database.PORT),
                    this.config.get(StandardKeys.Database.DATABASE),
                    this.config.get(StandardKeys.Database.QUERY_PARAMS)
            ));

            config.setUsername(this.config.get(StandardKeys.Database.USERNAME));
            config.setPassword(this.config.get(StandardKeys.Database.PASSWORD));

            config.setMaximumPoolSize(this.config.get(StandardKeys.Database.POOL_SIZE));
            config.setMinimumIdle(this.config.get(StandardKeys.Database.MIN_IDLE));
            config.setMaxLifetime(TimeUnit.SECONDS.toMillis(this.config.get(StandardKeys.Database.MAX_LIFETIME)));
            config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(this.config.get(StandardKeys.Database.CONN_TIMEOUT)));
        }

        return new HikariDataSource(config);
    }

    private String createJdbcUrl(final String host, final int port, final String database, final String queryParams) {
        final StringBuilder builder = new StringBuilder()
                .append("jdbc:mysql://")
                .append(host)
                .append(":")
                .append(port)
                .append("/")
                .append(database);

        if (!isNullOrEmpty(queryParams)) builder.append(queryParams);

        return builder.toString();
    }

    public Connection openConnection() throws SQLException {
        return this.dataSource.get().getConnection();
    }

    @Override
    public void close() {
        this.dataSource.get().close();
    }
}
