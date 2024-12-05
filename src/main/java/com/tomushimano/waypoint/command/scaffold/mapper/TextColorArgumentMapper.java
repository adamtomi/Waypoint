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

public class TextColorArgumentMapper extends AbstractArgumentMapper<CommandSender, TextColor> {
    private static final TypeToken<TextColor> TYPE = TypeToken.get(TextColor.class);
    private static final char HASH = '#';
    private final MessageConfig messageConfig;

    @Inject
    public TextColorArgumentMapper(final MessageConfig messageConfig) {
        super(TYPE, false);
        this.messageConfig = messageConfig;
    }

    @Override
    public TextColor tryMap(final CommandContext context, final CommandInputTokenizer input) throws CommandException {
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

    /*
    @Override
    public List<String> complete(CommandContext context, String input) {
        return List.copyOf(NamedTextColor.NAMES.keys());
    }*/
}
