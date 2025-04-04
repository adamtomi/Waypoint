package com.tomushimano.waypoint.command;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tomushimano.waypoint.command.scaffold.CommandExceptionHandler;
import com.tomushimano.waypoint.command.scaffold.ConfirmationHandler;
import com.tomushimano.waypoint.command.scaffold.condition.VerboseConditionException;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.di.qualifier.Lang;
import com.tomushimano.waypoint.message.Messages;
import com.tomushimano.waypoint.util.ConcurrentUtil;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import grapefruit.command.CommandModule;
import grapefruit.command.argument.CommandArgument;
import grapefruit.command.argument.CommandArgumentException;
import grapefruit.command.argument.CommandChain;
import grapefruit.command.argument.DuplicateFlagException;
import grapefruit.command.argument.UnrecognizedFlagException;
import grapefruit.command.completion.CommandCompletion;
import grapefruit.command.dispatcher.CommandDispatcher;
import grapefruit.command.dispatcher.CommandExecutionException;
import grapefruit.command.dispatcher.CommandSyntaxException;
import grapefruit.command.dispatcher.config.DispatcherConfig;
import grapefruit.command.tree.NoSuchCommandException;
import grapefruit.command.util.key.Key;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.tomushimano.waypoint.util.ExceptionUtil.capture;
import static io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents.COMMANDS;

@Singleton
@NullMarked
public final class CommandService {
    /* Removes the leading '/' from command strings */
    private static final Logger LOGGER = NamespacedLoggerFactory.create(CommandService.class);
    /* Create a threadpool for command execution */
    private final ExecutorService executor = Executors.newCachedThreadPool(
            new ThreadFactoryBuilder().setNameFormat("waypoint-commands #%1$d").build()
    );
    private final DispatcherConfig<CommandSender> dispatcherConfig = DispatcherConfig.<CommandSender>builder()
            .eagerFlagCompletions()
            .register(this::onCommandRegistration)
            .build();
    private final CommandDispatcher<CommandSender> dispatcher = CommandDispatcher.using(this.dispatcherConfig);
    private final Set<Key<String>> knownRootCommands = new HashSet<>();
    private final Set<WpRootCommand> pendingRegistrations = new HashSet<>();
    private final Set<CommandModule<CommandSender>> commands;
    private final CommandExceptionHandler exceptionHandler;
    private final ConfirmationHandler confirmationHandler;
    private final Configurable config;

    @Inject
    public CommandService(
            final Set<CommandModule<CommandSender>> commands,
            final CommandExceptionHandler exceptionHandler,
            final ConfirmationHandler confirmationHandler,
            final @Lang Configurable config
    ) {
        this.commands = commands;
        this.exceptionHandler = exceptionHandler;
        this.confirmationHandler = confirmationHandler;
        this.config = config;
    }

    public void register(final LifecycleEventManager<Plugin> eventManager) {
        LOGGER.info("Registering commands...");
        this.dispatcher.subscribe(this.confirmationHandler);
        // Register command handlers
        this.dispatcher.register(this.commands);
        eventManager.registerEventHandler(COMMANDS, event -> registerPendingCommands(event.registrar()));
    }

    public void unregister() {
        this.dispatcher.unregister(this.commands);
        this.dispatcher.unsubscribe(this.confirmationHandler);
        LOGGER.info("Shutting down async executor");
        ConcurrentUtil.terminate(this.executor, 1L);
    }

    private boolean onCommandRegistration(final CommandChain<CommandSender> chain) {
        register(chain);
        return true;
    }

    private void register(final CommandChain<CommandSender> chain) {
        final CommandArgument.Literal<CommandSender> root = chain.route().getFirst();
        if (this.knownRootCommands.contains(root.key())) return;

        this.knownRootCommands.add(root.key());
        final WpRootCommand command = new WpRootCommand(root, this);
        this.pendingRegistrations.add(command);
    }

    private void registerPendingCommands(final Commands registrar) {
        for (final WpRootCommand command : this.pendingRegistrations) {
            final CommandArgument.Literal<CommandSender> literal = command.literal;
            registrar.register(literal.name(), literal.aliases(), command);
        }

        LOGGER.info("Registered {} pending command(s)", this.pendingRegistrations.size());
        this.pendingRegistrations.clear();
    }

    private void performCommand(final CommandSender sender, final String commandLine) {
        this.executor.execute(() -> performCommand0(sender, commandLine));
    }

    // Forward the command to the dispatcher
    private void performCommand0(final CommandSender sender, final String commandLine) {
        try {
            this.dispatcher.dispatch(sender, commandLine);
        } catch (final CommandSyntaxException ex) {
            this.exceptionHandler.handleSyntaxError(sender, ex);
        } catch (final DuplicateFlagException ex) {
            this.exceptionHandler.handleDuplicateFlag(sender, ex);
        } catch (final NoSuchCommandException ex) {
            this.exceptionHandler.handleNoSuchCommand(sender, ex);
        } catch (final VerboseConditionException ex) {
          sender.sendMessage(ex.describeFailure());
        } catch (final UnrecognizedFlagException ex) {
            this.exceptionHandler.handleUnrecognizedFlag(sender, ex);
        } catch (final CommandArgumentException ex) {
            this.exceptionHandler.handleCommandArgumentError(sender, ex);
        } catch (final Throwable ex) {
            Messages.COMMAND__UNEXPECTED_ERROR.from(this.config).print(sender);
            // Extract cause. CommandExecutionException wraps other exceptions, so
            // just call getCause() on it to unwrap the exception we're interested in.
            final Throwable cause = ex instanceof CommandExecutionException
                    ? ex.getCause()
                    : ex;
            capture(cause, "Failed to execute command: '/%s'.".formatted(commandLine), LOGGER);
        }
    }

    private List<String> listCompletions(final CommandSender sender, final String commandLine) {
        return this.dispatcher.complete(sender, commandLine)
                .stream()
                .map(CommandCompletion::completion)
                .toList();
    }

    @NullMarked
    private static final class WpRootCommand implements BasicCommand {
        private final CommandArgument.Literal<CommandSender> literal;
        private final CommandService service;

        WpRootCommand(
                final CommandArgument.Literal<CommandSender> literal,
                final CommandService service
        ) {
            this.literal = literal;
            this.service = service;
        }

        private String rebuildArgs(final String[] args) {
            final StringJoiner joiner = new StringJoiner(" ");
            joiner.add(this.literal.name());
            for (final String arg : args) joiner.add(arg);
            return joiner.toString();
        }

        @Override
        public void execute(final CommandSourceStack source, final String[] args) {
            this.service.performCommand(source.getSender(), rebuildArgs(args));
        }

        @Override
        public Collection<String> suggest(final CommandSourceStack source, final String[] args) {
            return this.service.listCompletions(source.getSender(), rebuildArgs(args));
        }
    }
}
