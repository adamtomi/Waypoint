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
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.dispatcher.input.CommandInputTokenizer;
import io.leangen.geantyref.TypeToken;
import org.bukkit.command.CommandSender;

public class IntArgumentMapper extends AbstractArgumentMapper<CommandSender, Integer> {
    private static final TypeToken<Integer> TYPE = TypeToken.get(Integer.class);
    private final int min;
    private final int max;
    private final MessageConfig messageConfig;

    @AssistedInject
    public IntArgumentMapper(final @Assisted("min") int min, final @Assisted("max") int max, final MessageConfig messageConfig) {
        super(TYPE, false);
        this.min = min;
        this.max = max;
        this.messageConfig = messageConfig;
    }

    @Override
    public Integer tryMap(final CommandContext<CommandSender> context, final CommandInputTokenizer input) throws CommandException {
        final String arg = input.readWord();

        try {
            final int value = Integer.parseInt(arg);
            if (value < this.min || value > this.max) {
                throw RichArgumentException.fromInput(input, arg, this.messageConfig.get(MessageKeys.Command.RANGE_ERROR)
                        .with(Placeholder.of("min", this.min)) // Only include min value as that's what we really care about here.
                        .make());
            }

            return value;
        } catch (final NumberFormatException ex) {
            throw RichArgumentException.fromInput(input, arg, this.messageConfig.get(MessageKeys.Command.MALFORMED_NUMBER).make());
        }
    }

    @AssistedFactory
    public interface Factory {

        IntArgumentMapper create(final @Assisted("min") int min, final @Assisted("max") int max);
    }
}
