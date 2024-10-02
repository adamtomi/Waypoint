package com.tomushimano.waypoint.core.hologram;

import com.google.common.collect.ImmutableList;
import com.tomushimano.waypoint.core.hologram.network.PacketContainer;
import com.tomushimano.waypoint.util.Memoized;
import com.tomushimano.waypoint.util.Position;
import com.tomushimano.waypoint.util.TriFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

import static java.util.Objects.requireNonNull;

final class HologramLineImpl implements HologramLine {
    /* Flags responsible for "styling" the armor stand the right way */
    private static final byte ARMOR_STAND_CLIENT_FLAGS = (byte) (ArmorStand.CLIENT_FLAG_SMALL // Make it small
            | ArmorStand.CLIENT_FLAG_NO_BASEPLATE // Make it have no baseplate
            | ArmorStand.CLIENT_FLAG_MARKER);
    /* The following list of entity metadata is applied to every single hologram line */
    private static final List<SynchedEntityData.DataValue<?>> ENTITY_DATA_TEMPLATE = List.of(
            SynchedEntityData.DataValue.create(EntityDataAccess.CUSTOM_NAME_VISIBLE, true), // Name should be visible
            SynchedEntityData.DataValue.create(EntityDataAccess.NO_GRAVITY, true), // Should have no gravity - we don't want hologram lines to "fall"
            SynchedEntityData.DataValue.create(EntityDataAccess.ARMOR_STAND_CLIENT_FLAGS, ARMOR_STAND_CLIENT_FLAGS), // Apply the above flags
            SynchedEntityData.DataValue.create(EntityDataAccess.ENTITY_CLIENT_FLAGS, (byte) (1 << Entity.FLAG_INVISIBLE)) // Make th armor stand invisible
    );
    // Factory generating the default spawn packet
    private static final TriFunction<Integer, UUID, Position, Packet<?>> SPAWN_PACKET_FACTPORY = (id, uniqueId, position) -> new ClientboundAddEntityPacket(
            id,
            uniqueId,
            position.getX(),
            position.getY(),
            position.getZ(),
            0.0F,
            0.0F,
            EntityType.ARMOR_STAND,
            0,
            Vec3.ZERO,
            0.0F
    );
    // Factory generating the entity data packet, that follows up the initial spawn packet
    private static final BiFunction<Integer, Component, Packet<?>> ENTITY_DATA_PACKET_FACTORY = (id, name) -> {
        List<SynchedEntityData.DataValue<?>> values = ImmutableList.<SynchedEntityData.DataValue<?>>builder()
                .addAll(ENTITY_DATA_TEMPLATE)
                .add(SynchedEntityData.DataValue.create(EntityDataAccess.CUSTOM_NAME, Optional.of(name)))
                .build();
        return new ClientboundSetEntityDataPacket(id, values);
    };
    /* Generate negative entity IDs so that they don't clash with anything. Not sure if it's a good idea, but it works nonetheless */
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);
    // Use this fake id as an entity ID.
    private final Memoized<Integer> fakeId = Memoized.of(ID_GENERATOR::decrementAndGet);
    // Generate a random UUID for this hologram line.
    private final UUID uniqueId = UUID.randomUUID();
    private final Component content;
    private final Position position;

    HologramLineImpl(Component content, Position position) {
        this.content = requireNonNull(content, "content cannot be null");
        this.position = requireNonNull(position, "position cannot be null");
    }

    @Override
    public PacketContainer spawnPacket() {
        return PacketContainer.of(
                SPAWN_PACKET_FACTPORY.apply(this.fakeId.get(), this.uniqueId, this.position),
                ENTITY_DATA_PACKET_FACTORY.apply(this.fakeId.get(), this.content)
        );
    }

    @Override
    public PacketContainer despawnPacket() {
        return PacketContainer.of(new ClientboundRemoveEntitiesPacket(this.fakeId.get()));
    }
}
