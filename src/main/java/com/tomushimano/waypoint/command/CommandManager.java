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
import grapefruit.command.dispatcher.CommandAuthorizationException;
import grapefruit.command.dispatcher.CommandDispatcher;
import grapefruit.command.dispatcher.CommandInvocationException;
import grapefruit.command.dispatcher.input.CommandSyntaxException;
import grapefruit.command.tree.NoSuchCommandException;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.tomushimano.waypoint.util.ExceptionUtil.capture;
import static java.lang.String.join;

public class CommandManager {
    private static final Logger LOGGER = NamespacedLoggerFactory.create(CommandManager.class);
    private static final Comparator<NoSuchCommandException.Entry> NSCE_ENTRY_COMPARATOR = Comparator.comparing(NoSuchCommandException.Entry::name);
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
        } catch (final CommandSyntaxException ex) {
            handleSyntaxError(sender, ex);

        } catch (final NoSuchCommandException ex) {
            sender.sendMessage(this.messageConfig.get(MessageKeys.Command.INVALID_ARGUMENT)
                    .with(
                            Placeholder.of("argument", ex.argument()),
                            Placeholder.of("input", ex.consumed())
                    )
                    .make());

            handleNoSuchCommand(sender, ex);

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

    private void handleNoSuchCommand(final CommandSender sender, final NoSuchCommandException ex) {
        final String[] split = ex.consumed().split(" ");
        final StringJoiner joiner = new StringJoiner(" ");

        for (int i = 0; i < split.length - 1; i++) joiner.add(split[i]);

        final String prefix = joiner.toString();

        final List<Component> options = ex.validAlternatives().stream()
                .sorted(NSCE_ENTRY_COMPARATOR)
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
}
