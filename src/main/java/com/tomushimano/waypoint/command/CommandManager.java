package com.tomushimano.waypoint.command;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tomushimano.waypoint.command.scaffold.CommandHolder;
import com.tomushimano.waypoint.command.scaffold.CustomConfigurer;
import com.tomushimano.waypoint.util.ConcurrentUtil;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import grapefruit.command.CommandException;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.dispatcher.CommandDispatcher;
import grapefruit.command.dispatcher.config.DefaultConfigurer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.UnaryOperator;

import static com.tomushimano.waypoint.command.scaffold.WaypointContextKeys.PLAYER_KEY;
import static com.tomushimano.waypoint.command.scaffold.WaypointContextKeys.SENDER_KEY;
import static com.tomushimano.waypoint.util.ExceptionUtil.capture;

public class CommandManager implements CommandExecutor, Listener {
    private static final Logger LOGGER = NamespacedLoggerFactory.create("CommandManager");
    private static final UnaryOperator<String> STRIP_LEADING_SLASH = in -> in.startsWith("/") ? in.substring(1) : in;
    private final Set<String> trackedAliases = new HashSet<>();
    private final ExecutorService executor = Executors.newCachedThreadPool(
            new ThreadFactoryBuilder().setNameFormat("waypoint-commands #%1$d").build()
    );
    private final JavaPlugin plugin;
    private final Set<CommandHolder> commandHolders;
    private final CommandDispatcher dispatcher;

    @Inject
    public CommandManager(JavaPlugin plugin, Set<CommandHolder> commandHolders, CustomConfigurer.Factory configurerFactory) {
        this.plugin = plugin;
        this.commandHolders = commandHolders;
        this.dispatcher = CommandDispatcher.using(DefaultConfigurer.getInstance(), configurerFactory.create(this));
    }

    public void register() {
        LOGGER.info("Registering commands...");
        // Register command handlers
        this.commandHolders.forEach(x -> x.registerCommands(this.dispatcher));
        // Register tab-completion listener
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    public void shutdown() {
        LOGGER.info("Shutting down async executor");
        ConcurrentUtil.terminate(this.executor, 1L);
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

        // Execute command asynchronously
        this.executor.execute(() -> runCommand(createContext(sender), lineBuilder.toString()));

        // Always return true. Not like it really matters
        return true;
    }

    private void runCommand(CommandContext context, String commandLine) {
        try {
            this.dispatcher.dispatch(context, commandLine);
        } catch (CommandException ex) {
            capture(ex, "Failed to execute command: '/%s'.".formatted(commandLine), LOGGER);
        }
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

        List<String> completions = this.dispatcher.complete(createContext(event.getSender()), String.join(" ", args));
        event.setCompletions(completions);
        event.setHandled(true);
    }

    private CommandContext createContext(CommandSender sender) {
        CommandContext context = new CommandContext();
        context.put(SENDER_KEY, sender);
        // If the current executor is a player, we need to store it accordingly
        if (sender instanceof Player player) context.put(PLAYER_KEY, player);

        return context;
    }
}
