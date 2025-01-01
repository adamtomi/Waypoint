package com.tomushimano.waypoint.command.scaffold.mapper;

import com.tomushimano.waypoint.command.scaffold.VerboseArgumentMappingException;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.di.qualifier.Lang;
import com.tomushimano.waypoint.message.Messages;
import grapefruit.command.argument.mapper.AbstractArgumentMapper;
import grapefruit.command.argument.mapper.ArgumentMappingException;
import grapefruit.command.completion.Completion;
import grapefruit.command.completion.CompletionSupport;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.dispatcher.input.CommandInputTokenizer;
import grapefruit.command.dispatcher.input.MissingInputException;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class TextColorArgumentMapper extends AbstractArgumentMapper<CommandSender, TextColor> {
    private static final String HEX_ALPHABET = "0123456789abcdef";
    private static final String[] HEX_CHAR_SET = HEX_ALPHABET.split("");
    private static final char HASH = '#';
    private final Configurable config;

    @Inject
    public TextColorArgumentMapper(final @Lang Configurable config) {
        super(TextColor.class, false);
        this.config = config;
    }

    @Override
    public TextColor tryMap(final CommandContext<CommandSender> context, final CommandInputTokenizer input) throws ArgumentMappingException, MissingInputException {
        final String value = input.readWord();
        if (value.charAt(0) == HASH) {
            final TextColor color = TextColor.fromCSSHexString(value);
            if (color == null) {
                throw new VerboseArgumentMappingException(Messages.COMMAND__MALFORMED_COLOR.from(this.config).comp());
            }

            return color;
        }

        final NamedTextColor color = NamedTextColor.NAMES.value(value);
        if (color == null) {
            throw new VerboseArgumentMappingException(Messages.COMMAND__NO_SUCH_COLOR.from(this.config, value).comp());
        }

        return color;
    }

    @Override
    public List<Completion> complete(final CommandContext<CommandSender> context, final String input) {
        if (!input.isEmpty() && input.charAt(0) == HASH) {
            if (input.length() > 7) {
                // The input is formatted as #xxxxxx, thus it's complete
                return List.of();
            } else if (input.length() == 7) {
                return CompletionSupport.strings(input);
            }

            for (int i = 1; i < input.length(); i++) {
                final char c = input.charAt(i);
                if (HEX_ALPHABET.indexOf(c) == -1) {
                    return List.of();
                }
            }

            return Arrays.stream(HEX_CHAR_SET)
                    .map(x -> input + x)
                    .map(Completion::completion)
                    .toList();
        }

        return Stream.concat(
                NamedTextColor.NAMES.keys().stream().map(Completion::completion),
                Stream.of(Completion.completion(String.valueOf(HASH)))
        ).toList();
    }
}
