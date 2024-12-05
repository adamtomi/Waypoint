package com.tomushimano.waypoint.command;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tomushimano.waypoint.command.scaffold.RichArgumentException;
import com.tomushimano.waypoint.command.scaffold.event.CommandExecutionRequest;
import com.tomushimano.waypoint.command.scaffold.event.TabCompletionRequest;
import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import com.tomushimano.waypoint.util.ConcurrentUtil;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import grapefruit.command.CommandModule;
import grapefruit.command.argument.CommandArgumentException;
import grapefruit.command.dispatcher.CommandAuthorizationException;
import grapefruit.command.dispatcher.CommandDispatcher;
import grapefruit.command.dispatcher.CommandInvocationException;
import grapefruit.command.tree.NoSuchCommandException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.tomushimano.waypoint.util.ExceptionUtil.capture;
import static java.lang.String.join;
import static net.kyori.adventure.text.Component.text;

public class CommandManager {
    private static final Logger LOGGER = NamespacedLoggerFactory.create(CommandManager.class);
    private final ExecutorService executor = Executors.newCachedThreadPool(
            new ThreadFactoryBuilder().setNameFormat("waypoint-commands #%1$d").build()
    );
    private final CommandDispatcher<CommandSender> dispatcher;
    private final Set<CommandModule<CommandSender>> commands;
    private final MessageConfig messageConfig;
    private final EventBus eventBus;

    @Inject
    public CommandManager(
            final CommandDispatcher<CommandSender> dispatcher,
            final Set<CommandModule<CommandSender>> commands,
            final EventBus eventBus,
            final MessageConfig messageConfig
    ) {
        this.dispatcher = dispatcher;
        this.commands = commands;
        this.messageConfig = messageConfig;
        this.eventBus = eventBus;
    }


    public void register() {
        LOGGER.info("Registering commands...");
        // Register command handlers
        this.dispatcher.register(this.commands);
        this.eventBus.register(this);
    }

    public void shutdown() {
        this.eventBus.unregister(this);
        this.dispatcher.unregister(this.commands);
        LOGGER.info("Shutting down async executor");
        ConcurrentUtil.terminate(this.executor, 1L);
    }

    @Subscribe
    public void handle(final CommandExecutionRequest request) {
        // Run the command asychronously
        this.executor.execute(() -> runCommand(request.getSender(), request.getCommand()));
    }

    @Subscribe
    public void handle(final TabCompletionRequest request) {
        final List<String> completions = this.dispatcher.complete(request.getSender(), request.getCommand());
        request.addCompletions(completions);
    }

    // Forward the command to the dispatcher
    private void runCommand(final CommandSender sender, final String commandLine) {
        try {
            this.dispatcher.dispatch(sender, commandLine);
        } catch (final CommandAuthorizationException ex) {
            sender.sendMessage(this.messageConfig.get(MessageKeys.Command.INSUFFICIENT_PERMISSIONS)
                    .with(Placeholder.of("permission", join(", ", ex.lacking())))
                    .make());
        } catch (final NoSuchCommandException ex) {
            sender.sendMessage(this.messageConfig.get(MessageKeys.Command.INVALID_ARGUMENT)
                    .with(
                            Placeholder.of("argument", ex.argument()),
                            Placeholder.of("input", ex.consumed())
                    )
                    .make());

            // TODO print available commands with paginator?

        } catch (final RichArgumentException ex) {
            sender.sendMessage(this.messageConfig.get(MessageKeys.Command.INVALID_ARGUMENT)
                    .with(
                            Placeholder.of("argument", ex.argument()),
                            Placeholder.of("input", ex.consumed())
                    )
                    .make());

            sender.sendMessage(ex.richMessage());
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
}
