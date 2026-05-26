package com.tomushimano.waypoint.core;

import com.tomushimano.waypoint.util.Position;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.block.impl.CraftLight;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

final class LightSourceImpl implements LightSource {
    private final Supplier<Position> position;
    private final IntSupplier level;

    LightSourceImpl(final Supplier<Position> position, final IntSupplier level) {
        this.position = requireNonNull(position, "position cannot be null");
        this.level = requireNonNull(level, "level cannot be null");
    }

    @Override
    public void show(final Player player) {
        final Location location = this.position.get().toLocation();
        final CraftLight blockData = (CraftLight) Material.LIGHT.createBlockData();
        blockData.setLevel(this.level.getAsInt());

        sendPacket(player, location, blockData);
    }

    @Override
    public void hide(final Player player) {
        final Location location = this.position.get().toLocation();
        final CraftBlock block = (CraftBlock) location.getWorld().getBlockAt(location);
        sendPacket(player, location, (CraftBlockData) block.getBlockData());
    }

    private void sendPacket(final Player player, final Location location, final CraftBlockData blockData) {
        final ClientboundBlockUpdatePacket packet = createPacket(location, blockData);
        ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    private ClientboundBlockUpdatePacket createPacket(final Location location, final CraftBlockData blockData) {
        return new ClientboundBlockUpdatePacket(
                new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ()),
                blockData.getState()
        );
    }
}
