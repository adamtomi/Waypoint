package com.tomushimano.waypoint.command.scaffold;

import com.tomushimano.waypoint.config.ConfigKey;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import com.tomushimano.waypoint.di.qualifier.Cmd;
import grapefruit.command.argument.CommandArgument;
import grapefruit.command.argument.CommandArgumentException;
import grapefruit.command.argument.CommandChain;
import grapefruit.command.argument.DuplicateFlagException;
import grapefruit.command.argument.UnrecognizedFlagException;
import grapefruit.command.argument.mapper.ArgumentMappingException;
import grapefruit.command.dispatcher.CommandSyntaxException;
import grapefruit.command.tree.NoSuchCommandException;
import grapefruit.command.tree.node.CommandNode;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;

import static com.tomushimano.waypoint.config.message.MessageKeys.messageKey;
import static java.lang.String.join;
import static java.util.stream.Collectors.joining;

public final class CommandExceptionHandler {
    /* Creates a comparator that compares command nodes based on their name */
    private static final Comparator<CommandNode> COMMAND_NODE_COMPARATOR =
            Comparator.comparing(CommandNode::name);
    private final MessageConfig messageConfig;
    private final Configurable commandConfig;

    @Inject
    public CommandExceptionHandler(final MessageConfig messageConfig, final @Cmd Configurable commandConfig) {
        this.messageConfig = messageConfig;
        this.commandConfig = commandConfig;
    }

    private static ConfigKey<String> dynamicKey(final String parent, final CommandArgument.Dynamic<?, ?> argument) {
        return messageKey("%s.%s".formatted(parent, argument.name()));
    }

    private static String formatFlag(final String name) {
        return "--%s".formatted(name);
    }

    private String translateArgument(final String parent, final CommandArgument.Dynamic<?, ?> argument) {
        return this.commandConfig.get(dynamicKey(parent, argument));
    }

    private String formatArgument(final String parent, final CommandArgument.Dynamic<?, ?> argument) {
        final boolean optional = argument.isFlag();
        final String prefix = this.messageConfig.get(optional
                ? MessageKeys.Command.ARG_OPTIONAL_OPEN
                : MessageKeys.Command.ARG_REQUIRED_OPEN).makeString();
        final String suffix = this.messageConfig.get(optional
                ? MessageKeys.Command.ARG_OPTIONAL_CLOSE
                : MessageKeys.Command.ARG_REQUIRED_CLOSE).makeString();

        final StringBuilder builder = new StringBuilder(prefix);
        if (optional) {
            // Append formatted flag name
            builder.append(formatFlag(argument.name()));
            if (!argument.asFlag().isPresence()) {
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
        sender.sendMessage(this.messageConfig.get(MessageKeys.Command.INVALID_ARGUMENT)
                .with(
                        Placeholder.of("argument", ex.argument()),
                        Placeholder.of("consumed", prefix),
                        Placeholder.of("remaining", ex.remaining().trim())
                )
                .make());
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
        sender.sendMessage(this.messageConfig.get(ex.reason().equals(CommandSyntaxException.Reason.TOO_FEW_ARGUMENTS)
                ? MessageKeys.Command.SYNTAX_ERROR_TOO_FEW
                : MessageKeys.Command.SYNTAX_ERROR_TOO_MANY).make());

        sender.sendMessage(this.messageConfig.get(MessageKeys.Command.SYNTAX_HINT)
                .with(Placeholder.of("syntax", formatSyntax(ex.chain())))
                .make());
    }

    public void handleNoSuchCommand(final CommandSender sender, final NoSuchCommandException ex) {
        final String prefix = extractPrefix(ex);
        if (!ex.argument().isEmpty()) printCommandArgPrefix(sender, ex, prefix);

        final List<Component> options = ex.alternatives().stream()
                .sorted(COMMAND_NODE_COMPARATOR)
                .map(x -> this.messageConfig.get(MessageKeys.Command.UNKNOWN_SUBCOMMAND_ENTRY).with(
                        Placeholder.of("prefix", prefix),
                        Placeholder.of("name", x.name()),
                        Placeholder.of("aliases", join(", ", x.aliases()))).make())
                .toList();

        sender.sendMessage(this.messageConfig.get(MessageKeys.Command.UNKNOWN_SUBCOMMAND).make());
        options.forEach(sender::sendMessage);
    }

    public void handleDuplicateFlag(final CommandSender sender, final DuplicateFlagException ex) {
        printCommandArgPrefix(sender, ex);
        sender.sendMessage(this.messageConfig.get(MessageKeys.Command.DUPLICATE_FLAG).make());
    }

    public void handleUnrecognizedFlag(final CommandSender sender, final UnrecognizedFlagException ex) {
        printCommandArgPrefix(sender, ex);
        sender.sendMessage(this.messageConfig.get(MessageKeys.Command.INVALID_FLAG)
                .with(Placeholder.of("flag", ex.exactFlag()))
                .make());
    }

    public void handleArgumentMappingError(final CommandSender sender, final ArgumentMappingException ex) {
        printCommandArgPrefix(sender, ex);
        // This cast is safe, because we always make sure to throw VerboseArgumentException instances
        final VerboseArgumentException cause = (VerboseArgumentException) ex.getCause();
        sender.sendMessage(cause.describeFailure());
    }
}
