package com.tomushimano.waypoint.core;

import com.tomushimano.waypoint.core.hologram.Hologram;
import com.tomushimano.waypoint.core.hologram.HologramFactory;
import com.tomushimano.waypoint.util.Position;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public class Waypoint {
    private final UUID uuid;
    private final UUID ownerId;
    private String name;
    private NamedTextColor color;
    private boolean global;
    private Position position;
    private Hologram hologram;

    private Waypoint(
            UUID uuid,
            UUID ownerId,
            String name,
            NamedTextColor color,
            boolean global,
            Position pos
    ) {
        this.uuid = requireNonNull(uuid, "uuid cannot be null");
        this.ownerId = requireNonNull(ownerId, "ownerId cannot be null");
        this.name = requireNonNull(name, "name cannot be null");
        this.color = color;
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

    public TextColor getColor() {
        return this.color;
    }

    public void setColor(NamedTextColor color) {
        this.color = color;
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

    public void render() {
        runTargeted(this.hologram::show);
    }

    public void hide() {
        runTargeted(this.hologram::hide);
    }

    public void setHologram(Hologram hologram) {
        this.hologram = requireNonNull(hologram, "hologram cannot be null");
    }

    private void runTargeted(Consumer<Player> action) {
        if (this.global) {
            Bukkit.getOnlinePlayers().forEach(action);
        } else {
            Player owner = Bukkit.getPlayer(this.ownerId);
            if (owner == null) throw new IllegalStateException("The owner of the waypoint (%s) is offline".formatted(this.ownerId));
            action.accept(owner);
        }
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

    @Singleton
    public static final class Factory {
        private final HologramFactory hologramFactory;

        @Inject
        public Factory(HologramFactory hologramFactory) {
            this.hologramFactory = hologramFactory;
        }

        public Waypoint create(
                UUID uuid,
                UUID ownerId,
                String name,
                NamedTextColor color,
                boolean global,
                Position pos
        ) {
            Waypoint waypoint = new Waypoint(uuid, ownerId, name, color, global, pos);
            waypoint.setHologram(this.hologramFactory.createHologram(waypoint));
            return waypoint;
        }
    }
}
