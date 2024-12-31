package com.tomushimano.waypoint.config.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Deprecated
public class MessageBuilder {
    private static final char AMPERSAND = '&';
    private static final char SECTION = '§';
    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();
    private final List<Placeholder> placeholders = new ArrayList<>();
    private final String messageBase;

    MessageBuilder(final String messageBase) {
        this.messageBase = requireNonNull(messageBase, "messageBase cannot be null");
    }

    public MessageBuilder with(final Placeholder... placeholders) {
        this.placeholders.addAll(List.of(placeholders));
        return this;
    }

    public String makeString() {
        String result = this.messageBase;
        for (final Placeholder placeholder : this.placeholders) result = placeholder.process(result);
        return result;
    }

    public Component make() {
        return this.serializer.deserialize(makeString());
    }

    public net.minecraft.network.chat.Component makeNMS() {
        return net.minecraft.network.chat.Component.literal(makeString().replace(AMPERSAND, SECTION));
    }
}
