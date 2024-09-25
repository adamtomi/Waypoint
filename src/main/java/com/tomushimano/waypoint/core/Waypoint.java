package com.tomushimano.waypoint.core;

import com.tomushimano.waypoint.core.hologram.Hologram;
import com.tomushimano.waypoint.util.Position;

import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public class Waypoint {
    private final Hologram internalHologram = Hologram.create(this);
    private final UUID uuid;
    private final UUID ownerId;
    private String name;
    private boolean global;
    private Position position;

    public Waypoint(UUID uuid, UUID ownerId, String name, boolean global, Position pos) {
        this.uuid = requireNonNull(uuid, "uuid cannot be null");
        this.ownerId = requireNonNull(ownerId, "ownerId cannot be null");
        this.name = requireNonNull(name, "name cannot be null");
        this.global = global;
        this.position = requireNonNull(pos, "pos cannot be null");
    }

    public UUID getUniqueId() {
        return this.uuid;
    }

    public UUID getOwnerId() {
        return this.ownerId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = requireNonNull(name, "name cannot be null");
    }

    public boolean isGlobal() {
        return this.global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public Position getPosition() {
        return this.position;
    }

    public void setPosition(Position position) {
        this.position = requireNonNull(position, "position cannot be null");
    }

    public Hologram getHandle() {
        return this.internalHologram;
    }

    public void render() {
        this.internalHologram.spawn();
    }

    public void hide() {
        this.internalHologram.despawn();
    }

    @Override
    public String toString() {
        return "Waypoint{uuid=%s, ownerId=%s, name=%s, global=%s, position=%s}".formatted(
                this.uuid, this.ownerId, this.name, this.global, this.position
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Waypoint waypoint = (Waypoint) o;
        return Objects.equals(this.uuid, waypoint.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.uuid);
    }
}
