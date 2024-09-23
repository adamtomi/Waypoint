package com.tomushimano.waypoint.command.scaffold.modifier;

import grapefruit.command.argument.CommandArgumentException;
import grapefruit.command.argument.modifier.ContextualModifier;
import grapefruit.command.util.key.Key;

import javax.inject.Inject;

public class MaxModifier implements ContextualModifier<String> {
    private static final Key<Integer> MAX_KEY = Key.named(Integer.class, "value");
    private final int maxLength;

    MaxModifier(ContextualModifier.Context context) {
        this.maxLength = context.require(MAX_KEY);
    }

    @Override
    public String apply(String input) throws CommandArgumentException {
        if (input.length() > this.maxLength) throw new CommandArgumentException(); // TODO error msg
        return input;
    }

    public static final class Factory implements ContextualModifier.Factory<String> {

        @Inject
        public Factory() {}

        @Override
        public ContextualModifier<String> createFromContext(ContextualModifier.Context context) {
            return new MaxModifier(context);
        }
    }
}
