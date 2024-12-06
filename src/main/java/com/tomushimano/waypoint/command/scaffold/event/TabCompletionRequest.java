package com.tomushimano.waypoint.command.scaffold.event;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Deprecated
public final class TabCompletionRequest extends CommandEvent {
    private final List<String> completions = new ArrayList<>();

    public TabCompletionRequest(CommandSender sender, String command) {
        super(sender, command);
    }

    public void addCompletions(Collection<String> completions) {
        this.completions.addAll(completions);
    }

    public List<String> getCompletions() {
        return List.copyOf(this.completions);
    }
}
