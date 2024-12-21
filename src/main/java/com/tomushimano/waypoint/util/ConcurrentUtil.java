package com.tomushimano.waypoint.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public final class ConcurrentUtil {
    private ConcurrentUtil() {
        throw new DontInvokeMe();
    }

    public static void terminate(final ExecutorService executor, final long timeoutSeconds) {
        try {
            // Attempt graceful shutdown
            if (!executor.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) executor.shutdownNow();
        } catch (final InterruptedException ex) {
            // It failed, we're shutting it down for good
            executor.shutdownNow();
        }
    }
}
