package com.tomushimano.waypoint.command.scaffold.bukkit;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.tomushimano.waypoint.command.scaffold.event.CommandExecutionRequest;
import com.tomushimano.waypoint.command.scaffold.event.TabCompletionRequest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.UnaryOperator;

import static java.lang.String.join;

@Singleton
public final class BukkitCommandControl implements CommandExecutor, Listener {
    private static final UnaryOperator<String> STRIP_LEADING_SLASH = in -> in.startsWith("/") ? in.substring(1) : in;
    private final Set<String> trackedAliases = new HashSet<>();
    private final EventBus eventBus;
    private final JavaPlugin plugin;

    @Inject
    public BukkitCommandControl(EventBus eventBus, JavaPlugin plugin) {
        this.eventBus = eventBus;
        this.plugin = plugin;
    }

    public void register() {
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
        this.trackedAliases.clear();
    }

    public void track(Collection<String> aliases) {
        this.trackedAliases.addAll(aliases);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Build command line
        StringJoiner lineBuilder = new StringJoiner(" ");
        lineBuilder.add(label);
        for (String arg : args) lineBuilder.add(arg);

        // Dispatch command event
        this.eventBus.post(new CommandExecutionRequest(sender, lineBuilder.toString()));

        // Always return true. Not like it really matters
        return true;
    }

    @EventHandler
    public void onTabComplete(AsyncTabCompleteEvent event) {
        String buffer = event.getBuffer();
        // If this is not a command, we don't want to proceed
        if ((!event.isCommand() && !buffer.startsWith("/")) || buffer.indexOf(' ') == -1) return;

        /*
         * Collect args into a list. The list needs to be mutable, that's
         * why List#of is not an option here.
         */
        List<String> args = Lists.newArrayList(buffer.split(" ", -1));

        // Get rid of leading '/' character
        String root = STRIP_LEADING_SLASH.apply(args.getFirst());

        // See, if we need to provide completions for this event.
        // If we don't track this alias currently, we don't proceed.
        if (this.trackedAliases.stream().noneMatch(root::equalsIgnoreCase)) return;

        // Replace the first argument so that it doesn't contain the leading '/' anymore.
        if (args.size() > 1) args.set(0, root);

        TabCompletionRequest request = new TabCompletionRequest(event.getSender(), join(" ", args));
        this.eventBus.post(request);

        event.setCompletions(request.getCompletions());
        event.setHandled(true);
    }
}
