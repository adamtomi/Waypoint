package com.tomushimano.waypoint.command.scaffold.mapper;

import com.tomushimano.waypoint.command.scaffold.RichArgumentException;
import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import grapefruit.command.CommandException;
import grapefruit.command.argument.mapper.AbstractArgumentMapper;
import grapefruit.command.argument.mapper.builtin.StringArgumentMapper;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.dispatcher.input.CommandInputTokenizer;
import io.leangen.geantyref.TypeToken;
import org.bukkit.command.CommandSender;

import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.UNICODE_CHARACTER_CLASS;

public class VarcharArgumentMapper extends AbstractArgumentMapper<CommandSender, String> {
    private static final TypeToken<String> TYPE = TypeToken.get(String.class);
    private static final int CUTOFF = 15;
    private static final Pattern PATTERN = Pattern.compile("\\w+", CASE_INSENSITIVE | UNICODE_CHARACTER_CLASS);
    private final StringArgumentMapper<CommandSender> delegateMapper;
    private final MessageConfig messageConfig;
    private final int maxLength;

    @AssistedInject
    public VarcharArgumentMapper(final MessageConfig messageConfig, final @Assisted int maxLength) {
        super(TYPE, false);
        if (maxLength <= 0) throw new IllegalArgumentException("Max length needs to be positive");
        this.messageConfig = messageConfig;
        this.maxLength = maxLength;

        this.delegateMapper = StringArgumentMapper.<CommandSender>builder()
                .test(PATTERN, (arg, input) ->
                        RichArgumentException.fromInput(input, arg, this.messageConfig.get(MessageKeys.Command.REGEX_ERROR)
                                .with(Placeholder.of("regex", PATTERN.pattern()))
                                .make())
                ).asWord();
    }

    @Override
    public String tryMap(final CommandContext<CommandSender> context, final CommandInputTokenizer input) throws CommandException {
        final String word = this.delegateMapper.tryMap(context, input);
        if (word.length() > this.maxLength) {
            final String normalized = word.length() > CUTOFF
                ? word.substring(0, CUTOFF)
                : word;

            throw RichArgumentException.fromInput(input, word, this.messageConfig.get(MessageKeys.Command.MAX_LENGTH)
                    .with(Placeholder.of("max", this.maxLength), Placeholder.of("argument", normalized))
                    .make());
        }

        return word;
    }

    @AssistedFactory
    public interface Factory {

        VarcharArgumentMapper create(final int maxLength);
    }
}
