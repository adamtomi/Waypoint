package com.tomushimano.waypoint.command.scaffold.mapper;

import com.tomushimano.waypoint.command.scaffold.VerboseArgumentMappingException;
import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import grapefruit.command.CommandException;
import grapefruit.command.argument.mapper.AbstractArgumentMapper;
import grapefruit.command.argument.mapper.ArgumentMapper;
import grapefruit.command.argument.mapper.ArgumentMappingException;
import grapefruit.command.argument.mapper.builtin.StringArgumentMapper;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.dispatcher.input.CommandInputTokenizer;
import grapefruit.command.dispatcher.input.MissingInputException;
import io.leangen.geantyref.TypeToken;
import org.bukkit.command.CommandSender;

import java.util.Set;
import java.util.regex.Pattern;

import static grapefruit.command.argument.mapper.builtin.StringArgumentMapper.regex;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.UNICODE_CHARACTER_CLASS;

public class VarcharArgumentMapper extends AbstractArgumentMapper<CommandSender, String> {
    private static final TypeToken<String> TYPE = TypeToken.get(String.class);
    private static final int CUTOFF = 15;
    private static final Pattern PATTERN = Pattern.compile("\\w+", CASE_INSENSITIVE | UNICODE_CHARACTER_CLASS);
    private final ArgumentMapper<CommandSender, String> delegateMapper;

    @AssistedInject
    public VarcharArgumentMapper(final MessageConfig messageConfig, final @Assisted int maxLength) {
        super(TYPE, false);

        this.delegateMapper = StringArgumentMapper.<CommandSender>word().with(Set.of(
                regex(PATTERN, () -> new VerboseArgumentMappingException(messageConfig.get(MessageKeys.Command.REGEX_ERROR)
                        .with(Placeholder.of("regex", PATTERN.pattern()))
                        .make())),
                new LengthFilter(messageConfig, maxLength)
        ));
    }

    @Override
    public String tryMap(final CommandContext<CommandSender> context, final CommandInputTokenizer input) throws ArgumentMappingException, MissingInputException {
        return this.delegateMapper.tryMap(context, input);
    }

    private static final class LengthFilter implements Filter<CommandSender, String> {
        private final MessageConfig messageConfig;
        private final int maxLength;

        private LengthFilter(final MessageConfig messageConfig, final int maxLength) {
            if (maxLength <= 0) throw new IllegalArgumentException("Max length needs to be positive");
            this.messageConfig = messageConfig;
            this.maxLength = maxLength;
        }

        @Override
        public boolean test(final CommandContext<CommandSender> commandContext, final String value) {
            return value.length() <= this.maxLength;
        }

        @Override
        public CommandException generateException(final CommandContext<CommandSender> commandContext, final String value) {
            final String normalized = value.length() > CUTOFF
                    ? value.substring(0, CUTOFF)
                    : value;

            return new VerboseArgumentMappingException(this.messageConfig.get(MessageKeys.Command.MAX_LENGTH)
                    .with(Placeholder.of("max", this.maxLength), Placeholder.of("argument", normalized))
                    .make());
        }
    }

    @AssistedFactory
    public interface Factory {

        VarcharArgumentMapper create(final int maxLength);
    }
}
