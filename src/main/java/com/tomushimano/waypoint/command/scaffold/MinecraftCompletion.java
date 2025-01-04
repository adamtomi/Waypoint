package com.tomushimano.waypoint.command.scaffold;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import grapefruit.command.completion.CommandCompletion;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

@NullMarked
public class MinecraftCompletion implements CommandCompletion, AsyncTabCompleteEvent.Completion {
    private final String completion;
    private final @Nullable Component tooltip;

    private MinecraftCompletion(final String completion, final @Nullable Component tooltip) {
        this.completion = requireNonNull(completion, "completion cannot be null");
        this.tooltip = tooltip;
    }

    public static MinecraftCompletion tooltip(final String completion, final Component tooltip) {
        requireNonNull(tooltip, "tooltip cannot be null");
        return new MinecraftCompletion(completion, tooltip);
    }

    public static MinecraftCompletion noTooltip(final String completion) {
        return new MinecraftCompletion(completion, null);
    }

    @Override
    public String suggestion() {
        return this.completion;
    }

    @Override
    public @Nullable Component tooltip() {
        return this.tooltip;
    }

    @Override
    public String completion() {
        return this.completion;
    }
}
