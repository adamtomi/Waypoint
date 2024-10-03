package com.tomushimano.waypoint.core.hologram;

import com.tomushimano.waypoint.core.hologram.network.PacketContainer;
import com.tomushimano.waypoint.util.Position;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

/**
 * Represents a single line of a hologram.
 * @see Hologram
 */
public interface HologramLine {

    /**
     * Return a {@link PacketContainer} instance containing all
     * {@link net.minecraft.network.protocol.Packet} instances
     * required to spawn this hologram line.
     */
    PacketContainer spawnPacket();

    /**
     * Return a {@link PacketContainer} instance containing all
     * {@link net.minecraft.network.protocol.Packet} instances
     * required to despawn this hologram line.
     */
    PacketContainer despawnPacket();

    /**
     * Create a new hologram line instance with the provided
     * content at the provided position.
     *
     * @param content The content
     * @param position The position
     * @return The created hologram line
     */
    static HologramLine create(Supplier<Component> content, Supplier<Position> position) {
        return new HologramLineImpl(content, position);
    }

    /**
     * Create a new empty hologram line instance at the
     * provided position.
     *
     * @param position The position
     * @return The created hologram line
     */
    static HologramLine empty(Supplier<Position> position) {
        return new HologramLineImpl(Component::empty, position);
    }
}
