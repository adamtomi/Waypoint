package com.tomushimano.waypoint.command.scaffold.mapper;

import com.tomushimano.waypoint.command.scaffold.VerboseArgumentMappingException;
import com.tomushimano.waypoint.message.Messages;
import grapefruit.command.argument.mapper.AbstractArgumentMapper;
import grapefruit.command.argument.mapper.ArgumentMappingException;
import grapefruit.command.completion.CompletionAccumulator;
import grapefruit.command.completion.CompletionBuilder;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.dispatcher.input.CommandInputTokenizer;
import grapefruit.command.dispatcher.input.MissingInputException;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;

public class TextColorArgumentMapper extends AbstractArgumentMapper<CommandSender, TextColor> {
    private static final TextColorArgumentMapper INSTANCE = new TextColorArgumentMapper();
    private static final String HEX_ALPHABET = "0123456789abcdef";
    private static final String[] HEX_CHAR_SET = HEX_ALPHABET.split("");
    private static final char HASH = '#';

    private TextColorArgumentMapper() {
        super(TextColor.class, false);
    }

    public static TextColorArgumentMapper textColor() {
        return INSTANCE;
    }

    @Override
    public TextColor tryMap(final CommandContext<CommandSender> context, final CommandInputTokenizer input) throws ArgumentMappingException, MissingInputException {
        final String value = input.readWord();
        if (value.charAt(0) == HASH) {
            final TextColor color = TextColor.fromCSSHexString(value);
            if (color == null) {
                throw new VerboseArgumentMappingException(config -> Messages.COMMAND__MALFORMED_COLOR.from(config).comp());
            }

            return color;
        }

        final NamedTextColor color = NamedTextColor.NAMES.value(value);
        if (color == null) {
            throw new VerboseArgumentMappingException(config -> Messages.COMMAND__NO_SUCH_COLOR.from(config, value).comp());
        }

        return color;
    }

    @Override
    public CompletionAccumulator complete(final CommandContext<CommandSender> context, final CompletionBuilder builder) {
        final String input = builder.input();
        if (!input.isEmpty() && input.charAt(0) == HASH) {
            if (input.length() > 7) {
                // The input is formatted as #xxxxxx, thus it's complete
                return builder.build();
            } else if (input.length() == 7) {
                return builder.includeString(input).build();
            }

            for (int i = 1; i < input.length(); i++) {
                final char c = input.charAt(i);
                if (HEX_ALPHABET.indexOf(c) == -1) {
                    return builder.build();
                }
            }

            return builder.includeStrings(HEX_CHAR_SET, x -> input + x).build();
        }

        return builder.includeStrings(NamedTextColor.NAMES.keys())
                .includeString(String.valueOf(HASH))
                .build();
    }
}
