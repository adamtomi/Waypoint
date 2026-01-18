package com.tomushimano.waypoint.core;

import com.tomushimano.waypoint.core.hologram.Hologram;
import com.tomushimano.waypoint.core.hologram.HologramFactory;
import com.tomushimano.waypoint.util.Position;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public class Waypoint implements Comparable<Waypoint> {
    private final UUID uuid;
    private final UUID ownerId;
    private String name;
    private TextColor color;
    private boolean isPublic;
    private Position position;
    private Hologram hologram;

    private Waypoint(
            final UUID uuid,
            final UUID ownerId,
            final String name,
            final TextColor color,
            final boolean isPublic,
            final Position pos
    ) {
        this.uuid = requireNonNull(uuid, "uuid cannot be null");
        this.ownerId = requireNonNull(ownerId, "ownerId cannot be null");
        this.name = requireNonNull(name, "name cannot be null");
        this.color = color;
        this.isPublic = isPublic;
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

    public void setName(final String name) {
        this.name = requireNonNull(name, "name cannot be null");
    }

    public TextColor getColor() {
        return this.color;
    }

    public void setColor(final TextColor color) {
        this.color = color;
    }

    public boolean isPublic() {
        return this.isPublic;
    }

    public void setPublic(final boolean aPublic) {
        this.isPublic = aPublic;
    }

    public Position getPosition() {
        return this.position;
    }

    public void setPosition(final Position position) {
        this.position = requireNonNull(position, "position cannot be null");
    }

    public void render(final Player player) {
        this.hologram.show(player);
    }

    public void hide(final Player player) {
        this.hologram.hide(player);
    }

    public void rerender(final Player player) {
        hide(player);
        render(player);
    }

    public void setHologram(final Hologram hologram) {
        this.hologram = requireNonNull(hologram, "hologram cannot be null");
    }

    public long distance(final Entity entity) {
        return (long) entity.getLocation().distance(this.position.toLocation());
    }

    @Override
    public String toString() {
        return "Waypoint{uuid=%s, ownerId=%s, name=%s, public=%s, position=%s}".formatted(
                this.uuid, this.ownerId, this.name, this.isPublic, this.position
        );
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Waypoint waypoint = (Waypoint) o;
        return Objects.equals(this.uuid, waypoint.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.uuid);
    }

    @Override
    public int compareTo(final Waypoint waypoint) {
        return this.name.compareToIgnoreCase(waypoint.name);
    }

    @Singleton
    public static final class Factory {
        private final HologramFactory hologramFactory;

        @Inject
        public Factory(final HologramFactory hologramFactory) {
            this.hologramFactory = hologramFactory;
        }

        public Waypoint create(
                final UUID uuid,
                final UUID ownerId,
                final String name,
                final @Nullable TextColor color,
                final boolean isPublic,
                final Position pos
        ) {
            final TextColor actualColor = color == null ? NamedTextColor.WHITE : color;
            final Waypoint waypoint = new Waypoint(uuid, ownerId, name, actualColor, isPublic, pos);
            waypoint.setHologram(this.hologramFactory.createHologram(waypoint));
            return waypoint;
        }
    }
}
