package com.tomushimano.waypoint.config;

import java.io.Serial;

public class MissingConfigurationEntryException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -2937754230866292610L;

    public MissingConfigurationEntryException(final String key, final String path) {
        throw new UnsupportedOperationException("Configuration section at \"%s\" does not contain key \"%s\"".formatted(
                path,
                key
        ));
    }
}
