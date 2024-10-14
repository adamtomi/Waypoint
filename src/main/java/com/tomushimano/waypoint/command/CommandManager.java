package com.tomushimano.waypoint.command;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tomushimano.waypoint.command.scaffold.CommandModule;
import com.tomushimano.waypoint.command.scaffold.CustomConfigurer;
import com.tomushimano.waypoint.command.scaffold.RichCommandException;
import com.tomushimano.waypoint.command.scaffold.SyntaxFormatter;
import com.tomushimano.waypoint.command.scaffold.condition.VerboseCondition;
import com.tomushimano.waypoint.command.scaffold.event.CommandExecutionRequest;
import com.tomushimano.waypoint.command.scaffold.event.TabCompletionRequest;
import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import com.tomushimano.waypoint.util.ConcurrentUtil;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import grapefruit.command.argument.CommandArgumentException;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.dispatcher.CommandDispatcher;
import grapefruit.command.dispatcher.CommandInvocationException;
import grapefruit.command.dispatcher.condition.UnfulfilledConditionException;
import grapefruit.command.dispatcher.config.DefaultConfigurer;
import grapefruit.command.dispatcher.syntax.CommandSyntaxException;
import grapefruit.command.dispatcher.tree.CommandGraph;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.tomushimano.waypoint.command.scaffold.WaypointContextKeys.PLAYER_KEY;
import static com.tomushimano.waypoint.command.scaffold.WaypointContextKeys.SENDER_KEY;
import static com.tomushimano.waypoint.util.ExceptionUtil.capture;

public class CommandManager {
    private static final Logger LOGGER = NamespacedLoggerFactory.create(CommandManager.class);
    private final SyntaxFormatter syntaxFormatter = new SyntaxFormatter();
    private final ExecutorService executor = Executors.newCachedThreadPool(
            new ThreadFactoryBuilder().setNameFormat("waypoint-commands #%1$d").build()
    );
    private final Set<CommandModule> commandModules;
    private final MessageConfig messageConfig;
    private final EventBus eventBus;
    private final CommandDispatcher dispatcher;

    @Inject
    public CommandManager(
            Set<CommandModule> commandModules,
            CustomConfigurer configurer,
            EventBus eventBus,
            MessageConfig messageConfig
    ) {
        this.commandModules = commandModules;
        this.messageConfig = messageConfig;
        this.eventBus = eventBus;
        this.dispatcher = CommandDispatcher.using(DefaultConfigurer.getInstance(), configurer);
    }

    public void register() {
        LOGGER.info("Registering commands...");
        // Register command handlers
        this.commandModules.forEach(x -> x.registerCommands(this.dispatcher));
        this.eventBus.register(this);
    }

    public void shutdown() {
        this.eventBus.unregister(this);
        LOGGER.info("Shutting down async executor");
        ConcurrentUtil.terminate(this.executor, 1L);
    }

    @Subscribe
    public void handle(CommandExecutionRequest request) {
        // Run the command asychronously
        this.executor.execute(() -> runCommand(request.getSender(), request.getCommand()));
    }

    @Subscribe
    public void handle(TabCompletionRequest request) {
        List<String> completions = this.dispatcher.complete(createContext(request.getSender()), request.getCommand());
        request.addCompletions(completions);
    }

    // Forward the command to the dispatcher
    private void runCommand(CommandSender sender, String commandLine) {
        try {
            this.dispatcher.dispatch(createContext(sender), commandLine);
        } catch (UnfulfilledConditionException ex) {
            /*
             * This cast is safe, because there are no built-in conditions
             * in grapefruit currently, and our custom ones will all implement
             * VerboseCondition.
             */
            Component message = ((VerboseCondition) ex.condition()).describeFailure();
            sender.sendMessage(message);
        } catch (CommandGraph.NoSuchCommandException ex) {
            sender.sendMessage(this.messageConfig.get(MessageKeys.Command.UNKNOWN_SUBCOMMAND)
                    .with(Placeholder.of("command", ex.name()))
                    .make());
        } catch (CommandSyntaxException ex) {
          sender.sendMessage(this.messageConfig.get(MessageKeys.Command.SYNTAX_ERROR).make());
          ex.correctSyntax().map(this.syntaxFormatter)
                  .map(x -> this.messageConfig.get(MessageKeys.Command.SYNTAX_HINT)
                          .with(Placeholder.of("syntax", x)).make())
                  .ifPresent(sender::sendMessage);
        } catch (RichCommandException ex) {
            sender.sendMessage(ex.message());
        } catch (CommandArgumentException ex) {
            sender.sendMessage(this.messageConfig.get(MessageKeys.Command.INVALID_ARGUMENT)
                    .with(Placeholder.of("argument", ex.input()))
                    .make());
        } catch (Throwable ex) {
            sender.sendMessage(this.messageConfig.get(MessageKeys.Command.UNEXPECTED_ERROR).make());
            // Extract cause. CommandInvocationException wraps other exceptions, so
            // just call getCause(), if we're dealing with that
            Throwable cause = ex instanceof CommandInvocationException
                    ? ex.getCause()
                    : ex;
            capture(cause, "Failed to execute command: '/%s'.".formatted(commandLine), LOGGER);
        }
    }

    private CommandContext createContext(CommandSender sender) {
        CommandContext context = new CommandContext();
        context.put(SENDER_KEY, sender);
        // If the current executor is a player, we need to store it accordingly
        if (sender instanceof Player player) context.put(PLAYER_KEY, player);

        return context;
    }
}
