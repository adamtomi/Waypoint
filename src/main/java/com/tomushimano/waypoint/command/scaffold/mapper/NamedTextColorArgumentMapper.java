package com.tomushimano.waypoint.command.scaffold.mapper;

import com.tomushimano.waypoint.command.scaffold.RichCommandException;
import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import grapefruit.command.runtime.CommandException;
import grapefruit.command.runtime.argument.mapper.ArgumentMapper;
import grapefruit.command.runtime.dispatcher.CommandContext;
import grapefruit.command.runtime.dispatcher.input.StringReader;
import net.kyori.adventure.text.format.NamedTextColor;

import javax.inject.Inject;
import java.util.List;

@Deprecated
public class NamedTextColorArgumentMapper implements ArgumentMapper<NamedTextColor> {
    private final MessageConfig messageConfig;

    @Inject
    public NamedTextColorArgumentMapper(MessageConfig messageConfig) {
        this.messageConfig = messageConfig;
    }

    @Override
    public NamedTextColor tryMap(CommandContext context, StringReader input) throws CommandException {
        String value = input.readSingle();
        NamedTextColor color = NamedTextColor.NAMES.value(value);
        if (color == null) {
            throw new RichCommandException(this.messageConfig.get(MessageKeys.Command.NO_SUCH_COLOR)
                    .with(Placeholder.of("name", value))
                    .make());
        }

        return color;
    }

    @Override
    public List<String> complete(CommandContext context, String input) {
        return List.copyOf(NamedTextColor.NAMES.keys());
    }
}
