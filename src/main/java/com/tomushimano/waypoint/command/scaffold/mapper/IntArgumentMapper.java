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
import grapefruit.command.argument.mapper.builtin.NumericArgumentMapper;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.dispatcher.input.CommandInputTokenizer;
import grapefruit.command.dispatcher.input.MissingInputException;
import org.bukkit.command.CommandSender;

public class IntArgumentMapper extends AbstractArgumentMapper<CommandSender, Integer> {
    private final ArgumentMapper<CommandSender, Integer> delegateMapper;

    @AssistedInject
    public IntArgumentMapper(final @Assisted("min") int min, final @Assisted("max") int max, final MessageConfig messageConfig) {
        super(Integer.class, false);
        this.delegateMapper = NumericArgumentMapper.<CommandSender>intMapper(() -> new VerboseArgumentMappingException(messageConfig.get(MessageKeys.Command.MALFORMED_NUMBER)
                        .make())).with(new RangeFilter(messageConfig, min, max));
    }

    @Override
    public Integer tryMap(final CommandContext<CommandSender> context, final CommandInputTokenizer input) throws ArgumentMappingException, MissingInputException {
        return this.delegateMapper.tryMap(context, input);
    }

    @AssistedFactory
    public interface Factory {

        IntArgumentMapper create(final @Assisted("min") int min, final @Assisted("max") int max);
    }

    private static final class RangeFilter implements ArgumentMapper.Filter<CommandSender, Integer> {
        private final MessageConfig messageConfig;
        private final int min;
        private final int max;

        private RangeFilter(final MessageConfig messageConfig, final int min, final int max) {
            if (min > max) {
                throw new IllegalArgumentException("Expected min to be smaller than max");
            }

            this.messageConfig = messageConfig;
            this.min = min;
            this.max = max;
        }

        @Override
        public boolean test(final CommandContext<CommandSender> context, Integer value) {
            return value >= this.min && value <= this.max;
        }

        @Override
        public CommandException generateException(final CommandContext<CommandSender> context, Integer value) {
            return new VerboseArgumentMappingException(this.messageConfig.get(MessageKeys.Command.RANGE_ERROR)
                    .with(Placeholder.of("min", this.min)) // Only include min value as that's what we really care about here.
                    .make());
        }
    }
}
