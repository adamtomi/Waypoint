package com.tomushimano.waypoint.datastore.impl;

import com.tomushimano.waypoint.WaypointPlugin;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.datastore.Storage;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import com.tomushimano.waypoint.util.Position;
import net.kyori.adventure.text.format.NamedTextColor;
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
    private final FutureFactory futureFactory = new FutureFactory();
    private final ConnectionFactory connectionFactory;
    private final Waypoint.Factory waypointFactory;

    @Inject
    public SQLStorage(ConnectionFactory connectionFactory, Waypoint.Factory waypointFactory) {
        this.connectionFactory = connectionFactory;
        this.waypointFactory = waypointFactory;
    }

    private List<String> readDeployFile() throws IOException {
        List<String> result = new ArrayList<>();
        StringBuilder commandBuilder = new StringBuilder();
        try (InputStream in = WaypointPlugin.class.getResourceAsStream("/deploy.sql")) {
            if (in == null) throw new IOException("Deploy file does not exist");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                for (String line : reader.lines().toList()) {
                    // Ignore comments
                    if (line.startsWith("--")) continue;
                    // Handle cases, where a comment appears after the line itself.
                    String sanitized = line.trim().split("--")[0];
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
    public boolean connect() {
        try (Connection conn = this.connectionFactory.openConnection();
             Statement stmt = conn.createStatement()) {
            List<String> commands = readDeployFile();
            for (String command : commands) stmt.addBatch(command);
            stmt.executeBatch();

            LOGGER.info("Connection established successfully!");
            return true;
        } catch (IOException | SQLException ex) {
            capture(ex, "Failed to connect to database", LOGGER);
        }

        return false;
    }

    @Override
    public void disconnect() {
        LOGGER.info("Closing connection...");
        this.connectionFactory.close();
        this.futureFactory.close();
    }

    private String insertionSQL() {
        return "INSERT INTO `waypoints` VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT(`id`) DO UPDATE SET `name` = ?, `global` = ?, `world` = ?, `x` = ?, `y` = ?, `z` = ?";
    }

    private void fillInPrepStmt(PreparedStatement prepStmt, Waypoint waypoint, int startIdx, boolean includeIds) throws SQLException {
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
    public CompletableFuture<?> save(Waypoint waypoint) {
        return this.futureFactory.futureOf(() -> {
            try (Connection conn = this.connectionFactory.openConnection();
                 PreparedStatement prepStmt = conn.prepareStatement(insertionSQL())) {
                fillInPrepStmt(prepStmt, waypoint, 1, true);
                fillInPrepStmt(prepStmt, waypoint, 10, false);

                prepStmt.execute();
            }
        });
    }

    @Override
    public CompletableFuture<?> remove(Waypoint waypoint) {
        return this.futureFactory.futureOf(() -> {
            try (Connection conn = this.connectionFactory.openConnection();
                 PreparedStatement prepStmt = conn.prepareStatement("DELETE FROM `waypoints` WHERE `id` = ?")) {
                prepStmt.setString(1, waypoint.getUniqueId().toString());

                prepStmt.execute();
            }
        });
    }

    @Override
    public CompletableFuture<Set<Waypoint>> loadAccessible(UUID playerId) {
        return this.futureFactory.futureOf(() -> {
            try (Connection conn = this.connectionFactory.openConnection();
                PreparedStatement prepStmt = conn.prepareStatement("SELECT * FROM `waypoints` WHERE `ownerId` = ? OR `global` = true")) {
                prepStmt.setString(1, playerId.toString());

                ResultSet results = prepStmt.executeQuery();
                Set<Waypoint> waypoints = new HashSet<>();
                while (results.next()) {
                    UUID uniqueId = UUID.fromString(results.getString("id"));
                    UUID ownerId = UUID.fromString(results.getString("ownerId"));
                    String name = results.getString("name");
                    NamedTextColor color = NamedTextColor.namedColor(results.getInt("color"));
                    boolean global = results.getBoolean("global");
                    String world = results.getString("world");
                    double x = results.getDouble("x");
                    double y = results.getDouble("y");
                    double z = results.getDouble("z");

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
