package com.tomushimano.waypoint.util;

import com.tomushimano.waypoint.WaypointPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class IOUtil {
    private IOUtil() {
        throw new DontInvokeMe();
    }

    public static void copyResourceIfNotExists(Path parent, String resource) throws IOException {
        if (!Files.exists(parent)) {
            Files.createDirectory(parent);
        } else {
            if (!Files.isDirectory(parent)) throw new IllegalStateException("'%s' is not a directory");
        }

        Path destination = parent.resolve(resource);
        try (InputStream in = WaypointPlugin.class.getResourceAsStream("/%s".formatted(resource))) {
            if (in == null) throw new IOException("Cannot copy resource '%s' because it does not exist".formatted(resource));

            Files.copy(in, destination);
        }
    }
}
