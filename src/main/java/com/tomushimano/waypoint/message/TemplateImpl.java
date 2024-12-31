package com.tomushimano.waypoint.message;

import static java.util.Objects.requireNonNull;

final class TemplateImpl implements Template {
    private final String placeholder;
    private final String replacement;

    TemplateImpl(final String placeholder, final String replacement) {
        this.placeholder = requireNonNull(placeholder, "placeholder cannot be null");
        this.replacement = requireNonNull(replacement, "replacement cannot be null");
    }

    @Override
    public String placeholder() {
        return this.placeholder;
    }

    @Override
    public String replacement() {
        return this.replacement;
    }
}
