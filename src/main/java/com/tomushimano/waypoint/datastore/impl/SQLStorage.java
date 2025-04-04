package com.tomushimano.waypoint.datastore.impl;

import com.tomushimano.waypoint.WaypointPlugin;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.config.StandardKeys;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.datastore.Storage;
import com.tomushimano.waypoint.datastore.StorageKind;
import com.tomushimano.waypoint.di.qualifier.Cfg;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import com.tomushimano.waypoint.util.Position;
import net.kyori.adventure.text.format.TextColor;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.tomushimano.waypoint.util.ExceptionUtil.capture;

public class SQLStorage implements Storage {
    private static final Logger LOGGER = NamespacedLoggerFactory.create(SQLStorage.class);
    private final ConnectionFactory connectionFactory;
    private final Waypoint.Factory waypointFactory;
    private final FutureFactory futureFactory;
    private final Configurable config;

    @Inject
    public SQLStorage(
            final ConnectionFactory connectionFactory,
            final Waypoint.Factory waypointFactory,
            final FutureFactory futureFactory,
            final @Cfg Configurable config
    ) {
        this.connectionFactory = connectionFactory;
        this.waypointFactory = waypointFactory;
        this.futureFactory = futureFactory;
        this.config = config;
    }

    private List<String> readDeployFile() throws IOException {
        final List<String> result = new ArrayList<>();
        final StringBuilder commandBuilder = new StringBuilder();
        try (final InputStream in = WaypointPlugin.class.getResourceAsStream("/deploy.sql")) {
            if (in == null) throw new IOException("Deploy file does not exist");

            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                for (final String line : reader.lines().toList()) {
                    // Ignore comments
                    if (line.startsWith("--")) continue;
                    // Handle cases, where a comment appears after the line itself.
                    final String sanitized = line.trim().split("--")[0];
                    commandBuilder.append(sanitized);

                    // Hit the end of the command
                    if (line.endsWith(";")) {
                        // Add the command to the list
                        result.add(commandBuilder.toString());
                        // Reset builder
                        commandBuilder.setLength(0);
                    }
                }
            }
        }

        return result;
    }

    @Override
    public void connect() throws SQLException {
        try (final Connection conn = this.connectionFactory.openConnection();
             final Statement stmt = conn.createStatement()) {
            final List<String> commands = readDeployFile();
            for (final String command : commands) stmt.addBatch(command);
            stmt.executeBatch();

            LOGGER.info("Connection established successfully!");
        } catch (final IOException ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public void disconnect() {
        this.connectionFactory.close();
        this.futureFactory.close();
        LOGGER.info("Connection closed successfully!");
    }

    private String insertionSQL() {
        final StorageKind type = this.config.get(StandardKeys.Database.TYPE);
        return "INSERT INTO `waypoints` VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) %s SET `name` = ?, `color` = ?, `global` = ?, `world` = ?, `x` = ?, `y` = ?, `z` = ?"
                .formatted(type.upsert("id"));
    }

    private void fillInPrepStmt(
            final PreparedStatement prepStmt,
            final Waypoint waypoint,
            final int from,
            final boolean includeIds
    ) throws SQLException {
        int startIdx = from;
        // Fill in general data
        if (includeIds) {
            prepStmt.setString(startIdx++, waypoint.getUniqueId().toString());
            prepStmt.setString(startIdx++, waypoint.getOwnerId().toString());
        }

        prepStmt.setString(startIdx++, waypoint.getName());
        prepStmt.setInt(startIdx++, waypoint.getColor().value());
        prepStmt.setBoolean(startIdx++, waypoint.isGlobal());

        // Fill in location data
        Position pos = waypoint.getPosition();
        prepStmt.setString(startIdx++, pos.getWorldName());
        prepStmt.setDouble(startIdx++, pos.getX());
        prepStmt.setDouble(startIdx++, pos.getY());
        prepStmt.setDouble(startIdx, pos.getZ());
    }

    @Override
    public CompletableFuture<?> save(final Waypoint waypoint) {
        return this.futureFactory.futureOf(() -> {
            try (final Connection conn = this.connectionFactory.openConnection();
                 final PreparedStatement prepStmt = conn.prepareStatement(insertionSQL())) {
                fillInPrepStmt(prepStmt, waypoint, 1, true);
                fillInPrepStmt(prepStmt, waypoint, 10, false);

                prepStmt.execute();
            }
        });
    }

    @Override
    public CompletableFuture<?> remove(final Waypoint waypoint) {
        return this.futureFactory.futureOf(() -> {
            try (final Connection conn = this.connectionFactory.openConnection();
                 final PreparedStatement prepStmt = conn.prepareStatement("DELETE FROM `waypoints` WHERE `id` = ?")) {
                prepStmt.setString(1, waypoint.getUniqueId().toString());

                prepStmt.execute();
            }
        });
    }

    @Override
    public CompletableFuture<Set<Waypoint>> loadAccessible(final UUID playerId) {
        return this.futureFactory.futureOf(() -> {
            try (final Connection conn = this.connectionFactory.openConnection();
                 final PreparedStatement prepStmt = conn.prepareStatement("SELECT * FROM `waypoints` WHERE `ownerId` = ? OR `global` = true")) {
                prepStmt.setString(1, playerId.toString());

                final ResultSet results = prepStmt.executeQuery();
                final Set<Waypoint> waypoints = new HashSet<>();
                while (results.next()) {
                    final UUID uniqueId = UUID.fromString(results.getString("id"));
                    final UUID ownerId = UUID.fromString(results.getString("ownerId"));
                    final String name = results.getString("name");
                    final TextColor color = TextColor.color(results.getInt("color"));
                    final boolean global = results.getBoolean("global");
                    final String world = results.getString("world");
                    final double x = results.getDouble("x");
                    final double y = results.getDouble("y");
                    final double z = results.getDouble("z");

                    waypoints.add(this.waypointFactory.create(
                            uniqueId,
                            ownerId,
                            name,
                            color,
                            global,
                            new Position(world, x, y, z)
                    ));
                }

                return waypoints;
            }
        });
    }
}
