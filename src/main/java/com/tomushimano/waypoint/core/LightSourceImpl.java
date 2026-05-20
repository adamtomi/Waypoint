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

import static java.util.Objects.requireNonNull;

public final class LightSourceImpl implements LightSource {
    private static final int LEVEL = 10;
    private final Position position;

    public LightSourceImpl(final Position position) {
        this.position = requireNonNull(position, "position cannot be null");
    }

    @Override
    public void show(final Player player) {
        final Location location = this.position.toLocation();
        final CraftLight blockData = (CraftLight) Material.LIGHT.createBlockData();
        blockData.setLevel(LEVEL);

        sendPacket(player, location, blockData);
    }

    @Override
    public void hide(final Player player) {
        final Location location = this.position.toLocation();
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
