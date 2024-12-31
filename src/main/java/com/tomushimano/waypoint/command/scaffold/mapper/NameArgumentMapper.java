package com.tomushimano.waypoint.command.scaffold.mapper;

import com.tomushimano.waypoint.command.scaffold.VerboseArgumentMappingException;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.di.qualifier.Lang;
import com.tomushimano.waypoint.message.Messages;
import grapefruit.command.argument.mapper.AbstractArgumentMapper;
import grapefruit.command.argument.mapper.ArgumentMapper;
import grapefruit.command.argument.mapper.ArgumentMappingException;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.dispatcher.input.CommandInputTokenizer;
import grapefruit.command.dispatcher.input.MissingInputException;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static grapefruit.command.argument.mapper.builtin.StringArgumentMapper.regex;
import static grapefruit.command.argument.mapper.builtin.StringArgumentMapper.word;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.UNICODE_CHARACTER_CLASS;

public class NameArgumentMapper extends AbstractArgumentMapper<CommandSender, String> {
    private static final Pattern PATTERN = Pattern.compile("\\w+", CASE_INSENSITIVE | UNICODE_CHARACTER_CLASS);
    private final ArgumentMapper<CommandSender, String> delegateMapper;

    @Inject
    public NameArgumentMapper(final @Lang Configurable config) {
        super(String.class, false);
        final Supplier<ArgumentMappingException> exceptionSupplier = () -> new VerboseArgumentMappingException(Messages.COMMAND__REGEX_ERROR.from(config, PATTERN).comp());
        this.delegateMapper = delegate().filtering(new LengthFilter(config)).filtering(regex(PATTERN, exceptionSupplier));
    }

    private ArgumentMapper<CommandSender, String> delegate() {
        return word();
    }

    @Override
    public String tryMap(final CommandContext<CommandSender> context, final CommandInputTokenizer input) throws ArgumentMappingException, MissingInputException {
        return this.delegateMapper.tryMap(context, input);
    }

    private static final class LengthFilter implements Filter<CommandSender, String> {
        private static final int MAX_LENGTH = 255; // Limited by VARCHAR(255) in the database
        private static final int MAX_DISPLAYED_LENGTH = 15;
        private final Configurable config;

        private LengthFilter(final Configurable config) {
            this.config = config;
        }

        @Override
        public void test(final CommandContext<CommandSender> commandContext, final String value) throws ArgumentMappingException {
            if (value.length() > MAX_LENGTH) {
                final String normalized = "%s...".formatted(value.substring(0, MAX_DISPLAYED_LENGTH));
                throw new VerboseArgumentMappingException(Messages.COMMAND__MAX_LENGTH.from(this.config, normalized, MAX_LENGTH).comp());
            }
        }
    }
}
