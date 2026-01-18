package com.tomushimano.waypoint.command.scaffold.mapper;

import com.tomushimano.waypoint.command.scaffold.VerboseArgumentMappingException;
import com.tomushimano.waypoint.message.Messages;
import grapefruit.command.argument.mapper.AbstractArgumentMapper;
import grapefruit.command.argument.mapper.ArgumentMapper;
import grapefruit.command.argument.mapper.ArgumentMappingException;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.dispatcher.input.CommandInputTokenizer;
import grapefruit.command.dispatcher.input.MissingInputException;
import org.bukkit.command.CommandSender;

import static grapefruit.command.argument.mapper.builtin.NumericArgumentMapper.intMapper;

public class IntArgumentMapper extends AbstractArgumentMapper<CommandSender, Integer> {
    private final ArgumentMapper<CommandSender, Integer> delegateMapper;

    private IntArgumentMapper(final int min, final int max) {
        super(Integer.class, false);
        this.delegateMapper = delegate().filtering(new RangeFilter(min, max));
    }

    public static IntArgumentMapper positiveInt() {
        return new IntArgumentMapper(1, Integer.MAX_VALUE);
    }

    private ArgumentMapper<CommandSender, Integer> delegate() {
        return intMapper(() -> new VerboseArgumentMappingException(config -> Messages.COMMAND__MALFORMED_NUMBER.from(config).comp()));
    }

    @Override
    public Integer tryMap(final CommandContext<CommandSender> context, final CommandInputTokenizer input) throws ArgumentMappingException, MissingInputException {
        return this.delegateMapper.tryMap(context, input);
    }

    private static final class RangeFilter implements ArgumentMapper.Filter<CommandSender, Integer> {
        private final int min;
        private final int max;

        private RangeFilter(final int min, final int max) {
            if (min > max) {
                throw new IllegalArgumentException("Expected min to be smaller than max");
            }

            this.min = min;
            this.max = max;
        }

        @Override
        public void test(final CommandContext<CommandSender> context, Integer value) throws ArgumentMappingException {
            // return value >= this.min && value <= this.max;
            if (value < this.min || value > this.max) {
                // Only include min value as that's what we really care about here.
                throw new VerboseArgumentMappingException(config -> Messages.COMMAND__RANGE_ERROR.from(config, this.min).comp());
            }
        }
    }
}
