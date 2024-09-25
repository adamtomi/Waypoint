package com.tomushimano.waypoint.core.hologram;

import com.tomushimano.waypoint.util.DontInvokeMe;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;

public final class EntityDataAccess {
    public static final EntityDataAccessor<Boolean> CUSTOM_NAME_VISIBLE = lookupNative("DATA_CUSTOM_NAME_VISIBLE", Entity.class);
    public static final EntityDataAccessor<Optional<Component>> CUSTOM_NAME = lookupNative("DATA_CUSTOM_NAME", Entity.class);
    public static final EntityDataAccessor<Boolean> NO_GRAVITY = lookupNative("DATA_NO_GRAVITY", Entity.class);
    public static final EntityDataAccessor<Byte> ARMOR_STAND_CLIENT_FLAGS = lookupNative("DATA_CLIENT_FLAGS", ArmorStand.class);
    public static final EntityDataAccessor<Byte> ENTITY_CLIENT_FLAGS = lookupNative("DATA_SHARED_FLAGS_ID", Entity.class);

    private EntityDataAccess() {
        throw new DontInvokeMe();
    }

    @SuppressWarnings("unchecked")
    private static <T> EntityDataAccessor<T> lookupNative(String name, Class<? extends Entity> holder) {
        try {
            Field field = holder.getDeclaredField(name);
            if (!Modifier.isStatic(field.getModifiers())) {
                throw new IllegalStateException("Expected field '%s' to be static".formatted(name));
            }

            field.setAccessible(true);
            return (EntityDataAccessor<T>) field.get(null); // Fields are expected to be static, so passing null is fine
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }
}
