package com.tomushimano.waypoint.core.hologram;

import com.google.common.collect.ImmutableList;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.util.Memoized;
import com.tomushimano.waypoint.util.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.requireNonNull;

public final class HologramImpl implements Hologram {
    private static final byte ARMOR_STAND_CLIENT_FLAGS = (byte) (ArmorStand.CLIENT_FLAG_SMALL
            | ArmorStand.CLIENT_FLAG_NO_BASEPLATE
            | ArmorStand.CLIENT_FLAG_MARKER);
    private static final List<SynchedEntityData.DataValue<?>> ENTITY_DATA_TEMPLATE = List.of(
            SynchedEntityData.DataValue.create(EntityDataAccess.CUSTOM_NAME_VISIBLE, true),
            SynchedEntityData.DataValue.create(EntityDataAccess.NO_GRAVITY, true),
            SynchedEntityData.DataValue.create(EntityDataAccess.ARMOR_STAND_CLIENT_FLAGS, ARMOR_STAND_CLIENT_FLAGS),
            SynchedEntityData.DataValue.create(EntityDataAccess.ENTITY_CLIENT_FLAGS, (byte) (1 << Entity.FLAG_INVISIBLE))
    );
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);
    private final Memoized<Integer> fakeId = Memoized.of(ID_GENERATOR::decrementAndGet);
    private final Waypoint waypoint;

    HologramImpl(Waypoint waypoint) {
        this.waypoint = requireNonNull(waypoint, "waypoint cannot be null");
    }

    @Override
    public void spawn() {
        Player owner = getOwner();
        sendPackets(owner, createSpawnPacket(), createDataPacket());
    }

    private Player getOwner() {
        Player owner = Bukkit.getPlayer(this.waypoint.getOwnerId());
        if (owner == null) {
            throw new IllegalStateException("The owner of this waypoint is not online, cannot render.");
        }

        return owner;
    }

    private void sendPackets(Player player, Packet<?>... packets) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        for (Packet<?> packet : packets) serverPlayer.connection.send(packet);
    }

    private Packet<?> createSpawnPacket() {
        UUID entityId = this.waypoint.getUniqueId();
        Position position = this.waypoint.getPosition();

        return new ClientboundAddEntityPacket(
                this.fakeId.get(),
                entityId,
                position.getX(),
                position.getY() + 1,
                position.getZ(),
                0.0F,
                0.0F,
                EntityType.ARMOR_STAND,
                0,
                Vec3.ZERO,
                0.0F
        );
    }

    private Packet<?> createDataPacket() {
        List<SynchedEntityData.DataValue<?>> values = ImmutableList.<SynchedEntityData.DataValue<?>>builder()
                .addAll(ENTITY_DATA_TEMPLATE)
                .add(SynchedEntityData.DataValue.create(EntityDataAccess.CUSTOM_NAME, Optional.of(Component.literal(this.waypoint.getName()))))
                .build();
        return new ClientboundSetEntityDataPacket(
                this.fakeId.get(),
                values
        );
    }

    @Override
    public void despawn() {
        Player owner = getOwner();
        sendPackets(owner, new ClientboundRemoveEntitiesPacket(this.fakeId.get()));
    }
}
