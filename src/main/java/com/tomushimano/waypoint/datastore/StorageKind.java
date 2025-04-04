package com.tomushimano.waypoint.datastore;

public enum StorageKind {
    SQLITE("ON CONFLICT (`%s`) DO UPDATE"),
    MYSQL("ON DUPLCATE KEY UPDATE")
    ;

    private final String upsert;

    StorageKind(final String upsert) {
        this.upsert = upsert;
    }

    public String upsert(final String column) {
        return this.upsert.formatted(column);
    }
}
