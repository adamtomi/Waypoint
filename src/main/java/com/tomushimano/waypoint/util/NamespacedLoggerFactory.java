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

    /**
     * Retrieves the simple name of the provided class.
     * The difference between this method and
     * {@link Class#getSimpleName()} is that this takes
     * nested classes into account. So if class A has
     * a nested class B, loggerName(B.class) will return
     * "A$B" whereas {@link Class#getSimpleName()} returns
     * simply "B".
     *
     * @param clazz The class
     * @return The retrieved name
     */
    public static Logger create(Class<?> clazz) {
        String[] path = clazz.getName().split("\\.");
        String className = path[path.length - 1];
        return create(className);
    }
}
