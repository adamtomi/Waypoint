package com.tomushimano.waypoint.command.scaffold;

import grapefruit.command.dispatcher.syntax.CommandSyntax;

import java.util.StringJoiner;
import java.util.function.Function;

public class SyntaxFormatter implements Function<CommandSyntax, String> {
    private static final char REQUIRED_OPENING = '<';
    private static final char REQUIRED_CLOSING = '>';
    private static final char OPTIONAL_OPENING = '[';
    private static final char OPTIONAL_CLOSING = ']';

    @Override
    public String apply(CommandSyntax syntax) {
        StringJoiner joiner = new StringJoiner(" ");
        // Add route parts
        syntax.route().forEach(x -> joiner.add(x.primaryAlias()));
        // Add argument parts
        syntax.parts().stream()
                .map(x -> x.isOptional()
                        ? OPTIONAL_OPENING + x.format() + OPTIONAL_CLOSING
                        : REQUIRED_OPENING + x.format() + REQUIRED_CLOSING)
                .forEach(joiner::add);

        return joiner.toString();
    }
}
