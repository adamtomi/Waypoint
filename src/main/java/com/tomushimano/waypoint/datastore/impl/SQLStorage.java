package com.tomushimano.waypoint.datastore.impl;

import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.datastore.Storage;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SQLStorage implements Storage {
    private final ConnectionFactory connectionFactory;

    public SQLStorage(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public CompletableFuture<?> connect() {
        return null;
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
