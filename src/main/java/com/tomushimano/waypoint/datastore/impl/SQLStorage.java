package com.tomushimano.waypoint.datastore.impl;

import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.datastore.Storage;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SQLStorage implements Storage {
    private final ConnectionFactory connectionFactory;

    public SQLStorage(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    private String readDeployFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void connect() {
        try (Connection conn = this.connectionFactory.openConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(readDeployFile());
        } catch (SQLException ex) {

        }
    }

    @Override
    public void disconnect() {
        this.connectionFactory.close();
    }

    @Override
    public CompletableFuture<?> save(Waypoint waypoint) {
        return null;
    }

    @Override
    public CompletableFuture<Set<Waypoint>> loadAccessible(UUID playerId) {
        return null;
    }
}
