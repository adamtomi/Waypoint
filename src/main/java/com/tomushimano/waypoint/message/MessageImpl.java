package com.tomushimano.waypoint.message;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

final class MessageImpl implements Message {
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.legacyAmpersand();
    private static final char AMPERSAND = '&';
    private static final char SECTION = '§';
    private final String raw;

    MessageImpl(final String message) {
        this.raw = requireNonNull(message, "message cannot be null");
    }

    @Override
    public String raw() {
        return this.raw;
    }

    @Override
    public Component comp() {
        return SERIALIZER.deserialize(this.raw);
    }

    @Override
    public net.minecraft.network.chat.Component nms() {
        return net.minecraft.network.chat.Component.literal(this.raw.replace(AMPERSAND, SECTION));
    }

    @Override
    public void print(final Audience audience) {
        print(audience::sendMessage);
    }

    @Override
    public void printActionBar(final Audience audience) {
        print(audience::sendActionBar);
    }

    private void print(final Consumer<Component> delivery) {
        delivery.accept(comp());
    }
}
