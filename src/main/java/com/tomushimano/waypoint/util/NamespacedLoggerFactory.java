package com.tomushimano.waypoint.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NamespacedLoggerFactory {
    private static final String BASE_NAME = "Waypoint";

    private NamespacedLoggerFactory() {
        throw new DontInvokeMe();
    }

    public static Logger create(String name) {
        return LoggerFactory.getLogger("%s/%s".formatted(BASE_NAME, name));
    }
}
