package com.tomushimano.waypoint.command.scaffold.mapper;

import com.tomushimano.waypoint.command.scaffold.RichArgumentException;
import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import grapefruit.command.CommandException;
import grapefruit.command.argument.mapper.AbstractArgumentMapper;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.dispatcher.input.CommandInputTokenizer;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class TextColorArgumentMapper extends AbstractArgumentMapper<CommandSender, TextColor> {
    private static final TypeToken<TextColor> TYPE = TypeToken.get(TextColor.class);
    private static final String[] HEX_CHAR_SET = new String[] {
            "0", "1", "2", "3", "4", "5", "6", "7",
            "8", "9", "a", "b", "c", "d", "e", "f"
    };
    private static final char HASH = '#';
    private final MessageConfig messageConfig;

    @Inject
    public TextColorArgumentMapper(final MessageConfig messageConfig) {
        super(TYPE, false);
        this.messageConfig = messageConfig;
    }

    @Override
    public TextColor tryMap(final CommandContext<CommandSender> context, final CommandInputTokenizer input) throws CommandException {
        final String value = input.readWord();
        if (value.charAt(0) == HASH) {
            final TextColor color = TextColor.fromCSSHexString(value);
            if (color == null) {
                throw RichArgumentException.fromInput(input, value, this.messageConfig.get(MessageKeys.Command.MALFORMED_COLOR).make());
            }

            return color;
        }

        final NamedTextColor color = NamedTextColor.NAMES.value(value);
        if (color == null) {
            throw RichArgumentException.fromInput(input, value, this.messageConfig.get(MessageKeys.Command.NO_SUCH_COLOR)
                    .with(Placeholder.of("name", value))
                    .make());
        }

        return color;
    }

    @Override
    public List<String> complete(final CommandContext<CommandSender> context, final String input) {
        if (!input.isEmpty() && input.charAt(0) == HASH) {
            if (input.length() >= 7) {
                // The input is formatted as #xxxxxx, thus it's complete
                return List.of();
            }

            return Arrays.stream(HEX_CHAR_SET)
                    .map(x -> input + x)
                    .toList();
        }

        return Stream.concat(NamedTextColor.NAMES.keys().stream(), Stream.of(String.valueOf(HASH))).toList();
    }
}
