package com.tomushimano.waypoint.config.message;

import static java.util.Objects.requireNonNull;

public class Placeholder {
    private static final String NULL = "-";
    private static final char TEMPLATE_START = '{';
    private static final char TEMPLATE_END = '}';
    private final String placeholder;
    private final Object replacement;

    private Placeholder(final String placeholder, final Object replacement) {
        this.placeholder = requireNonNull(placeholder, "placeholder cannot be null");
        this.replacement = replacement;
    }

    public static Placeholder of(final String placeholder, final Object replacement) {
        return new Placeholder(TEMPLATE_START + placeholder + TEMPLATE_END, replacement);
    }

    public String process(final String message) {
        final String replacement = this.replacement != null
                ? this.replacement.toString()
                : NULL;
        return message.replace(this.placeholder, replacement);
    }
}
