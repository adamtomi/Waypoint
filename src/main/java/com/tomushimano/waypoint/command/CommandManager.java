package com.tomushimano.waypoint.command;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tomushimano.waypoint.command.scaffold.CommandDispatcherFactory;
import com.tomushimano.waypoint.command.scaffold.RichArgumentException;
import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import com.tomushimano.waypoint.util.ConcurrentUtil;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import grapefruit.command.CommandModule;
import grapefruit.command.argument.CommandArgumentException;
import grapefruit.command.argument.DuplicateFlagException;
import grapefruit.command.argument.UnrecognizedFlagException;
import grapefruit.command.dispatcher.CommandAuthorizationException;
import grapefruit.command.dispatcher.CommandDispatcher;
import grapefruit.command.dispatcher.CommandInvocationException;
import grapefruit.command.dispatcher.input.CommandSyntaxException;
import grapefruit.command.tree.NoSuchCommandException;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.UnaryOperator;

import static com.tomushimano.waypoint.util.ExceptionUtil.capture;
import static grapefruit.command.util.StringUtil.containsIgnoreCase;
import static java.lang.String.join;

@Singleton
public final class CommandManager implements CommandExecutor, Listener {
    /* Removes the leading '/' from command strings */
    private static final UnaryOperator<String> STRIP_LEADING_SLASH = in -> in.startsWith("/") ? in.substring(1) : in;
    private static final Logger LOGGER = NamespacedLoggerFactory.create(CommandManager.class);
    /* Creates a comparator that compares NoSuchCommandException$Alternative-s based on their name */
    private static final Comparator<NoSuchCommandException.Alternative> NSCE_ALTERNATIVE_COMPARATOR =
            Comparator.comparing(NoSuchCommandException.Alternative::name);
    /* Create a threadpool for command execution */
    private final ExecutorService executor = Executors.newCachedThreadPool(
            new ThreadFactoryBuilder().setNameFormat("waypoint-commands #%1$d").build()
    );
    /* Completions will be provided by this manager for tracked aliases only. */
    private final Set<String> trackedAliases = new HashSet<>();
    private final CommandDispatcher<CommandSender> dispatcher;
    private final Set<CommandModule<CommandSender>> commands;
    private final JavaPlugin plugin;
    private final MessageConfig messageConfig;

    @Inject
    public CommandManager(
            final CommandDispatcherFactory dispatcherFactory,
            final Set<CommandModule<CommandSender>> commands,
            final JavaPlugin plugin,
            final MessageConfig messageConfig
    ) {
        this.dispatcher = dispatcherFactory.create(this);
        this.commands = commands;
        this.plugin = plugin;
        this.messageConfig = messageConfig;
    }

    public void register() {
        LOGGER.info("Registering commands...");
        // this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
        // Register command handlers
        this.dispatcher.register(this.commands);
    }

    public void shutdown() {
        // HandlerList.unregisterAll(this);
        this.trackedAliases.clear();
        this.dispatcher.unregister(this.commands);
        LOGGER.info("Shutting down async executor");
        ConcurrentUtil.terminate(this.executor, 1L);
    }

    public void track(Collection<String> aliases) {
        this.trackedAliases.addAll(aliases);
    }

    public void untrack(Collection<String> aliases) {
        this.trackedAliases.removeAll(aliases);
    }

    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull Command command,
            final @NotNull String label,
            final @NotNull String[] args
    ) {
        // Build command line
        final StringJoiner lineBuilder = new StringJoiner(" ");
        lineBuilder.add(label);
        for (final String arg : args) lineBuilder.add(arg);

        // Dispatch command asynchronously
        this.executor.execute(() -> runCommand(sender, lineBuilder.toString()));

        // Always return true. Not like it really matters
        return true;
    }

    @EventHandler
    public void onTabComplete(final AsyncTabCompleteEvent event) {
        final String buffer = event.getBuffer();
        // If this is not a command, we don't want to proceed
        if ((!event.isCommand() && !buffer.startsWith("/")) || buffer.indexOf(' ') == -1) return;

        /*
         * Collect args into a list. The list needs to be mutable, that's
         * why List#of is not an option here.
         */
        final List<String> args = Lists.newArrayList(buffer.split(" ", -1));

        // Get rid of leading '/' character
        final String root = STRIP_LEADING_SLASH.apply(args.getFirst());

        // See, if we need to provide completions for this event.
        // If we don't track this alias currently, we don't proceed.
        if (!containsIgnoreCase(root, this.trackedAliases)) return;

        // Replace the first argument so that it doesn't contain the leading '/' anymore.
        if (args.size() > 1) args.set(0, root);

        final List<String> completions = this.dispatcher.complete(event.getSender(), join(" ", args));

        event.setCompletions(completions);
        event.setHandled(true);
    }

    // Forward the command to the dispatcher
    private void runCommand(final CommandSender sender, final String commandLine) {
        try {
            this.dispatcher.dispatch(sender, commandLine);
        } catch (final CommandAuthorizationException ex) {
            sender.sendMessage(this.messageConfig.get(MessageKeys.Command.INSUFFICIENT_PERMISSIONS)
                    .with(Placeholder.of("permission", join(", ", ex.lacking())))
                    .make());
        } catch (final CommandSyntaxException ex) {
            handleSyntaxError(sender, ex);
        } catch (final DuplicateFlagException ex) {
            handleDuplicateFlag(sender, ex);
        } catch (final NoSuchCommandException ex) {
            handleNoSuchCommand(sender, ex);
        } catch (final RichArgumentException ex) {
            printCommandArgPrefix(sender, ex);
            sender.sendMessage(ex.richMessage());
        } catch (final UnrecognizedFlagException ex) {
            handleUnrecognizedFlag(sender, ex);
        } catch (final Throwable ex) {
            sender.sendMessage(this.messageConfig.get(MessageKeys.Command.UNEXPECTED_ERROR).make());
            // Extract cause. CommandInvocationException wraps other exceptions, so
            // just call getCause() on it to unwrap the exception we're interested in.
            final Throwable cause = ex instanceof CommandInvocationException
                    ? ex.getCause()
                    : ex;
            capture(cause, "Failed to execute command: '/%s'.".formatted(commandLine), LOGGER);
        }
    }

    private void handleDuplicateFlag(final CommandSender sender, final DuplicateFlagException ex) {
        printCommandArgPrefix(sender, ex);
        sender.sendMessage(this.messageConfig.get(MessageKeys.Command.DUPLICATE_FLAG).make());
    }

    private void handleUnrecognizedFlag(final CommandSender sender, final UnrecognizedFlagException ex) {
        printCommandArgPrefix(sender, ex);
        sender.sendMessage(this.messageConfig.get(MessageKeys.Command.INVALID_FLAG)
                .with(Placeholder.of("flag", ex.exactFlag()))
                .make());
    }

    private void handleNoSuchCommand(final CommandSender sender, final NoSuchCommandException ex) {
        final String prefix = extractPrefix(ex);
        printCommandArgPrefix(sender, ex, prefix);

        final List<Component> options = ex.alternatives().stream()
                .sorted(NSCE_ALTERNATIVE_COMPARATOR)
                .map(x -> this.messageConfig.get(MessageKeys.Command.UNKNOWN_SUBCOMMAND_ENTRY).with(
                        Placeholder.of("prefix", prefix),
                        Placeholder.of("name", x.name()),
                        Placeholder.of("aliases", join(", ", x.aliases()))).make())
                .toList();

        sender.sendMessage(this.messageConfig.get(MessageKeys.Command.UNKNOWN_SUBCOMMAND).make());
        options.forEach(sender::sendMessage);
    }

    private void handleSyntaxError(final CommandSender sender, final CommandSyntaxException ex) {
        sender.sendMessage(this.messageConfig.get(ex.reason().equals(CommandSyntaxException.Reason.TOO_FEW_ARGUMENTS)
                ? MessageKeys.Command.SYNTAX_ERROR_TOO_FEW
                : MessageKeys.Command.SYNTAX_ERROR_TOO_MANY).make());
        // TODO print correct syntax (with localized names where possible)
    }

    private String extractPrefix(final CommandArgumentException ex) {
        final String[] split = ex.consumed().split(" ");
        final StringJoiner joiner = new StringJoiner(" ");

        for (int i = 0; i < split.length - 1; i++) joiner.add(split[i]);

        return joiner.toString();
    }

    private void printCommandArgPrefix(final CommandSender sender, final CommandArgumentException ex) {
        final String prefix = extractPrefix(ex);
        printCommandArgPrefix(sender, ex, prefix);
    }

    private void printCommandArgPrefix(final CommandSender sender, final CommandArgumentException ex, final String prefix) {
        sender.sendMessage(this.messageConfig.get(MessageKeys.Command.INVALID_ARGUMENT)
                .with(
                        Placeholder.of("argument", ex.argument()),
                        Placeholder.of("consumed", prefix),
                        Placeholder.of("remaining", ex.remaining().trim())
                )
                .make());
    }
}
