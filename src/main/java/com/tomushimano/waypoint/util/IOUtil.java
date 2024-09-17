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
        // Check, if parent does not exist
        if (!Files.exists(parent)) {
            // Create it if necessary
            Files.createDirectory(parent);
        } else {
            // Looks like parent does exist, but is not a directory. Uh-oh
            if (!Files.isDirectory(parent)) throw new IllegalStateException("'%s' is not a directory");
        }

        Path destination = parent.resolve(resource);
        // We don't want to replace existing files, so just return here
        if (Files.exists(destination)) return;

        try (InputStream in = WaypointPlugin.class.getResourceAsStream("/%s".formatted(resource))) {
            // Resource not found, nothing to copy
            if (in == null) throw new IOException("Cannot copy resource '%s' because it does not exist".formatted(resource));

            Files.copy(in, destination);
        }
    }
}
