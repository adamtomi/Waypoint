package com.tomushimano.waypoint.core.hologram.network;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;

import static java.util.Objects.requireNonNull;

final class PacketContainerImpl implements PacketContainer {
    private final List<Packet<?>> packets;

    PacketContainerImpl(List<Packet<?>> packets) {
        this.packets = requireNonNull(packets, "packets cannot be null");
    }

    @Override
    public void send(Player player) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        this.packets.forEach(serverPlayer.connection::send);
    }
}
