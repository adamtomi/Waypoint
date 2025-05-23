package com.tomushimano.waypoint.command;

import com.tomushimano.waypoint.command.scaffold.CommandExceptionHandler;
import com.tomushimano.waypoint.command.scaffold.ConfirmationHandler;
import com.tomushimano.waypoint.command.scaffold.condition.VerboseConditionException;
import com.tomushimano.waypoint.util.FutureFactory;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import grapefruit.command.CommandModule;
import grapefruit.command.argument.CommandArgument;
import grapefruit.command.argument.CommandArgumentException;
import grapefruit.command.argument.CommandChain;
import grapefruit.command.argument.DuplicateFlagException;
import grapefruit.command.argument.UnrecognizedFlagException;
import grapefruit.command.completion.CommandCompletion;
import grapefruit.command.dispatcher.CommandDispatcher;
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

import static io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents.COMMANDS;

@Singleton
@NullMarked
public final class CommandService {
    private static final Logger LOGGER = NamespacedLoggerFactory.create(CommandService.class);
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
    private final FutureFactory futureFactory;

    @Inject
    public CommandService(
            final Set<CommandModule<CommandSender>> commands,
            final CommandExceptionHandler exceptionHandler,
            final ConfirmationHandler confirmationHandler,
            final FutureFactory futureFactory
    ) {
        this.commands = commands;
        this.exceptionHandler = exceptionHandler;
        this.confirmationHandler = confirmationHandler;
        this.futureFactory = futureFactory;
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
        LOGGER.info("Commands have been unregistered");
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
        this.futureFactory.futureOf(() -> performCommand0(sender, commandLine));
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
            this.exceptionHandler.handleGenericCommandError(sender, commandLine, ex);
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
            final String[] adjustedArgs = args.length == 0
                    ? new String[] {" "}
                    : args;
            final String commandLine = rebuildArgs(adjustedArgs);
            return this.service.listCompletions(source.getSender(), commandLine);
        }
    }
}
