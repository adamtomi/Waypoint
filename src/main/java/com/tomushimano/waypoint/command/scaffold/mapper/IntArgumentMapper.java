package com.tomushimano.waypoint.command.scaffold.mapper;

import com.tomushimano.waypoint.command.scaffold.VerboseArgumentMappingException;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.di.qualifier.Lang;
import com.tomushimano.waypoint.message.Messages;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import grapefruit.command.argument.mapper.AbstractArgumentMapper;
import grapefruit.command.argument.mapper.ArgumentMapper;
import grapefruit.command.argument.mapper.ArgumentMappingException;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.dispatcher.input.CommandInputTokenizer;
import grapefruit.command.dispatcher.input.MissingInputException;
import org.bukkit.command.CommandSender;

import static grapefruit.command.argument.mapper.builtin.NumericArgumentMapper.intMapper;

public class IntArgumentMapper extends AbstractArgumentMapper<CommandSender, Integer> {
    private final Configurable config;
    private final ArgumentMapper<CommandSender, Integer> delegateMapper;

    @AssistedInject
    public IntArgumentMapper(final @Assisted("min") int min, final @Assisted("max") int max, final @Lang Configurable config) {
        super(Integer.class, false);
        this.config = config;
        this.delegateMapper = delegate().filtering(new RangeFilter(config, min, max));
    }

    private ArgumentMapper<CommandSender, Integer> delegate() {
        return intMapper(() -> new VerboseArgumentMappingException(Messages.COMMAND__MALFORMED_NUMBER.from(this.config).comp()));
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
        private final Configurable config;
        private final int min;
        private final int max;

        private RangeFilter(final Configurable config, final int min, final int max) {
            if (min > max) {
                throw new IllegalArgumentException("Expected min to be smaller than max");
            }

            this.config = config;
            this.min = min;
            this.max = max;
        }

        @Override
        public void test(final CommandContext<CommandSender> context, Integer value) throws ArgumentMappingException {
            // return value >= this.min && value <= this.max;
            if (value < this.min || value > this.max) {
                // Only include min value as that's what we really care about here.
                throw new VerboseArgumentMappingException(Messages.COMMAND__RANGE_ERROR.from(this.config, this.min).comp());
            }
        }
    }
}
