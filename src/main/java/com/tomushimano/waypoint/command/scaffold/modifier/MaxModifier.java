package com.tomushimano.waypoint.command.scaffold.modifier;

import com.tomushimano.waypoint.command.scaffold.RichCommandException;
import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import grapefruit.command.runtime.argument.CommandArgumentException;
import grapefruit.command.runtime.argument.modifier.ArgumentModifier;
import grapefruit.command.runtime.util.key.Key;

import javax.inject.Inject;

public class MaxModifier implements ArgumentModifier<String> {
    private static final int MAX_LENGTH_IN_ERR_MSG = 50;
    private static final Key<Integer> MAX_KEY = Key.named(Integer.class, "value");
    private final int maxLength;
    private final MessageConfig messageConfig;

    MaxModifier(ArgumentModifier.Context context, MessageConfig messageConfig) {
        this.maxLength = context.require(MAX_KEY);
        this.messageConfig = messageConfig;
    }

    @Override
    public String apply(String input) throws CommandArgumentException {
        if (input.length() > this.maxLength) {
            String argumentToShow = input.length() > MAX_LENGTH_IN_ERR_MSG
                    ? input.substring(0, MAX_LENGTH_IN_ERR_MSG - 1)
                    : input;

            throw new RichCommandException(this.messageConfig.get(MessageKeys.Command.MAX_LENGTH)
                    .with(Placeholder.of("argument", argumentToShow), Placeholder.of("max", this.maxLength))
                    .make());
        }

        return input;
    }

    public static final class Factory implements ArgumentModifier.Factory<String> {
        private final MessageConfig messageConfig;

        @Inject
        public Factory(MessageConfig messageConfig) {
            this.messageConfig = messageConfig;
        }

        @Override
        public ArgumentModifier<String> createFromContext(ArgumentModifier.Context context) {
            return new MaxModifier(context, this.messageConfig);
        }
    }
}
