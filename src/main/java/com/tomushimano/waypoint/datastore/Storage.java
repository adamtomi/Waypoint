package com.tomushimano.waypoint.datastore;

import com.tomushimano.waypoint.core.Waypoint;

import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Storage {

    void connect() throws SQLException;

    void disconnect();

    CompletableFuture<?> save(final Waypoint waypoint);

    CompletableFuture<?> remove(final Waypoint waypoint);

    CompletableFuture<Set<Waypoint>> loadAccessible(final UUID playerId);
}
