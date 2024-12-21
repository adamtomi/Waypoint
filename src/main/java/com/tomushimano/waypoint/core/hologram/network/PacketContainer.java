package com.tomushimano.waypoint.core.hologram.network;

import net.minecraft.network.protocol.Packet;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * A simple container containing an arbitrary number of packets.
 */
public interface PacketContainer {

    /**
     * Send all packets to the provided player.
     *
     * @param player The player
     */
    void send(final Player player);

    /**
     * Create a new {@link PacketContainer} from the provided packets.
     *
     * @param packets The packets
     * @return The created container
     */
    static PacketContainer of(final Packet<?>... packets) {
        return new PacketContainerImpl(List.of(packets));
    }
}
