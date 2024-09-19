package com.tomushimano.waypoint.config.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class MessageBuilder {
    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();
    private final List<Placeholder> placeholders = new ArrayList<>();
    private final String messageBase;

    MessageBuilder(String messageBase) {
        this.messageBase = requireNonNull(messageBase, "messageBase cannot be null");
    }

    public MessageBuilder with(Placeholder... placeholders) {
        this.placeholders.addAll(List.of(placeholders));
        return this;
    }

    public Component make() {
        String result = this.messageBase;
        for (Placeholder placeholder : this.placeholders) result = placeholder.process(result);
        return this.serializer.deserialize(result);
    }
}
