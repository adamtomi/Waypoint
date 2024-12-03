package com.tomushimano.waypoint.command.scaffold.mapper;

import com.tomushimano.waypoint.config.message.MessageConfig;
import grapefruit.command.argument.mapper.AbstractArgumentMapper;
import grapefruit.command.argument.mapper.MappingResult;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.dispatcher.input.CommandInputTokenizer;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;

public class NamedTextColorArgumentMapper extends AbstractArgumentMapper<CommandSender, NamedTextColor> {
    private static final TypeToken<NamedTextColor> TYPE = TypeToken.get(NamedTextColor.class);
    private final MessageConfig messageConfig;

    @Inject
    public NamedTextColorArgumentMapper(MessageConfig messageConfig) {
        super(TYPE, false);
        this.messageConfig = messageConfig;
    }

    // TODO proper exceptions
    @Override
    public MappingResult<NamedTextColor> tryMap(CommandContext context, CommandInputTokenizer input) {
        String value = input.readWord();
        NamedTextColor color = NamedTextColor.NAMES.value(value);
        if (color == null) {
            /* throw new RichCommandException(this.messageConfig.get(MessageKeys.Command.NO_SUCH_COLOR)
                    .with(Placeholder.of("name", value))
                    .make());

             */
            return MappingResult.fail(input, value, new RuntimeException());
        }

        return MappingResult.ok(color);
    }

    /*
    @Override
    public List<String> complete(CommandContext context, String input) {
        return List.copyOf(NamedTextColor.NAMES.keys());
    }*/
}
