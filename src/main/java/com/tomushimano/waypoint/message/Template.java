package com.tomushimano.waypoint.message;

public interface Template {
    String OPENING = "{";
    String CLOSING = "}";
    String NULL = "-";

    String placeholder();

    String replacement();

    default String process(final String input) {
        return input.replace(placeholder(), replacement());
    }

    static Template template(final String placeholder, final Object replacement) {
        return new TemplateImpl(wrap(placeholder), replacement == null ? NULL : replacement.toString());
    }

    static Template template(final String placeholder, final double replacement) {
        return template(placeholder, "%.3f".formatted(replacement));
    }

    static String wrap(final String placeholder) {
        return OPENING + placeholder + CLOSING;
    }
}
