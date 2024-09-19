package com.tomushimano.waypoint.datastore.impl;

import com.tomushimano.waypoint.config.ConfigHolder;
import com.tomushimano.waypoint.config.StandardKeys;
import com.tomushimano.waypoint.util.Memoized;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.requireNonNull;

public class ConnectionFactory implements AutoCloseable {
    private final Memoized<HikariDataSource> dataSource = Memoized.of(this::createDataSource);
    private final JavaPlugin plugin;
    private final ConfigHolder config;
    private final boolean filebased;

    public ConnectionFactory(JavaPlugin plugin, ConfigHolder config, boolean filebased) {
        this.plugin = requireNonNull(plugin, "plugin cannot be null");
        this.config = requireNonNull(config, "config cannot be null");
        this.filebased = filebased;
    }

    private HikariDataSource createDataSource() {
        // Create config containing common settings
        HikariConfig config = new HikariConfig();
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setPoolName("WaypointPool");

        if (this.filebased) {
            config.setDriverClassName("org.sqlite.JDBC");
            config.setJdbcUrl("jdbc:sqlite:/%s".formatted(this.plugin.getDataPath().resolve(this.config.get(StandardKeys.Database.FILE_NAME))));
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

    private String createJdbcUrl(String host, int port, String database, String queryParams) {
        StringBuilder builder = new StringBuilder()
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
