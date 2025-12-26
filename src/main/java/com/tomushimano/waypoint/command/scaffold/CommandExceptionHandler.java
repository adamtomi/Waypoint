package com.tomushimano.waypoint.command.scaffold;

import com.tomushimano.waypoint.config.ConfigKey;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.di.qualifier.Cmd;
import com.tomushimano.waypoint.di.qualifier.Lang;
import com.tomushimano.waypoint.message.Messages;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import grapefruit.command.argument.CommandArgument;
import grapefruit.command.argument.CommandArgumentException;
import grapefruit.command.argument.CommandChain;
import grapefruit.command.argument.DuplicateFlagException;
import grapefruit.command.argument.UnrecognizedFlagException;
import grapefruit.command.dispatcher.CommandExecutionException;
import grapefruit.command.dispatcher.CommandSyntaxException;
import grapefruit.command.tree.NoSuchCommandException;
import grapefruit.command.tree.node.CommandNode;
import org.bukkit.command.CommandSender;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;

import static com.tomushimano.waypoint.config.ConfigKey.fallbackToKey;
import static com.tomushimano.waypoint.util.ExceptionUtil.capture;
import static java.util.stream.Collectors.joining;

public final class CommandExceptionHandler {
    private static final Logger LOGGER = NamespacedLoggerFactory.create(CommandExceptionHandler.class);
    /* Creates a comparator that compares command nodes based on their name */
    private static final Comparator<CommandNode> COMMAND_NODE_COMPARATOR =
            Comparator.comparing(CommandNode::name);
    private final Configurable langConfig;
    private final Configurable commandConfig;

    @Inject
    public CommandExceptionHandler(final @Cmd Configurable commandConfig, final @Lang Configurable langConfig) {
        this.commandConfig = commandConfig;
        this.langConfig = langConfig;
    }

    private static ConfigKey<String> dynamicKey(final String parent, final CommandArgument.Dynamic<?, ?> argument) {
        return fallbackToKey("%s.%s".formatted(parent, argument.name()));
    }

    private static String formatFlag(final String name) {
        return "--%s".formatted(name);
    }

    private String translateArgument(final String parent, final CommandArgument.Dynamic<?, ?> argument) {
        return this.commandConfig.get(dynamicKey(parent, argument));
    }

    private String formatArgument(final String parent, final CommandArgument.Dynamic<?, ?> argument) {
        final boolean optional = argument.isFlag();
        final String prefix = optional
                ? Messages.COMMAND__ARG_OPTIONAL_OPEN.from(this.langConfig).raw()
                : Messages.COMMAND__ARG_REQUIRED_OPEN.from(this.langConfig).raw();
        final String suffix = optional
                ? Messages.COMMAND__ARG_OPTIONAL_CLOSE.from(this.langConfig).raw()
                : Messages.COMMAND__ARG_REQUIRED_CLOSE.from(this.langConfig).raw();

        final StringBuilder builder = new StringBuilder(prefix);
        if (optional) {
            // Append formatted flag name
            builder.append(formatFlag(argument.name()));
            if (!argument.asFlag().isBool()) {
                builder.append(" ").append(translateArgument(parent, argument));
            }
        } else {
            builder.append(translateArgument(parent, argument));
        }

        return builder.append(suffix).toString();
    }

    private String formatSyntax(final CommandChain<?> chain) {
        final StringJoiner joiner = new StringJoiner(" ");
        chain.route().forEach(x -> joiner.add(x.name()));
        final String parent = chain.route().stream()
                .skip(1L)
                .map(CommandArgument::name)
                .collect(joining("."));
        chain.arguments().forEach(x -> joiner.add(formatArgument(parent, x)));
        chain.flags().forEach(x -> joiner.add(formatArgument(parent, x)));

        return joiner.toString();
    }

    private void printCommandArgPrefix(final CommandSender sender, final CommandArgumentException ex, final String prefix) {
        Messages.COMMAND__INVALID_ARGUMENT.from(this.langConfig, prefix, ex.argument(), ex.remaining().trim()).print(sender);
    }

    private void printCommandArgPrefix(final CommandSender sender, final CommandArgumentException ex) {
        final String prefix = extractPrefix(ex);
        printCommandArgPrefix(sender, ex, prefix);
    }

    private String extractPrefix(final CommandArgumentException ex) {
        final String[] split = ex.consumed().split(" ");
        final StringJoiner joiner = new StringJoiner(" ");

        for (int i = 0; i < split.length - (ex.argument().isEmpty() ? 0 : 1); i++) joiner.add(split[i]);

        return joiner.toString();
    }

    public void handleSyntaxError(final CommandSender sender, final CommandSyntaxException ex) {
        (ex.reason().equals(CommandSyntaxException.Reason.TOO_FEW_ARGUMENTS) ? Messages.COMMAND__SYNTAX_ERROR_TOO_FEW : Messages.COMMAND__SYNTAX_ERROR_TOO_MANY)
                .from(this.langConfig)
                .print(sender);

        Messages.COMMAND__SYNTAX_HINT.from(this.langConfig, formatSyntax(ex.chain())).print(sender);
    }

    public void handleNoSuchCommand(final CommandSender sender, final NoSuchCommandException ex) {
        final String prefix = extractPrefix(ex);
        if (!ex.argument().isEmpty()) printCommandArgPrefix(sender, ex, prefix);

        final List<CommandNode> options = ex.alternatives().stream()
                .sorted(COMMAND_NODE_COMPARATOR)
                .toList();

        Messages.COMMAND__UNKNOWN_SUBCOMMAND.from(this.langConfig).print(sender);
        for (final CommandNode node : options) {
            Messages.COMMNAD__UNKNOWN_SUBCOMMAND_ENTRY.from(this.langConfig, prefix, node.name(), node.aliases()).print(sender);
        }
    }

    public void handleDuplicateFlag(final CommandSender sender, final DuplicateFlagException ex) {
        printCommandArgPrefix(sender, ex);
        Messages.COMMAND__DUPLICATE_FLAG.from(this.langConfig).print(sender);
    }

    public void handleUnrecognizedFlag(final CommandSender sender, final UnrecognizedFlagException ex) {
        printCommandArgPrefix(sender, ex);
        Messages.COMMAND__INVALID_FLAG.from(this.langConfig, ex.exactFlag()).print(sender);
    }

    public void handleCommandArgumentError(final CommandSender sender, final CommandArgumentException ex) {
        printCommandArgPrefix(sender, ex);
        // This cast is safe, because we always make sure to throw VerboseArgumentException instances
        final VerboseArgumentMappingException cause = (VerboseArgumentMappingException) ex.getCause();
        sender.sendMessage(cause.describeFailure());
    }

    public void handleGenericCommandError(final CommandSender sender, final String commandLine, final Throwable ex) {
        Messages.COMMAND__UNEXPECTED_ERROR.from(this.langConfig).print(sender);
        // Extract cause. CommandExecutionException wraps other exceptions, so
        // just call getCause() on it to unwrap the exception we're interested in.
        final Throwable cause = ex instanceof CommandExecutionException
                ? ex.getCause()
                : ex;
        capture(cause, "Failed to execute command: '/%s'.".formatted(commandLine), LOGGER);
    }
}
