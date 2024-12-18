package com.tomushimano.waypoint.datastore;

import com.tomushimano.waypoint.core.Waypoint;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Storage {

    boolean connect();

    void disconnect();

    CompletableFuture<?> save(Waypoint waypoint);

    CompletableFuture<?> remove(Waypoint waypoint);

    CompletableFuture<Set<Waypoint>> loadAccessible(UUID playerId);
}
