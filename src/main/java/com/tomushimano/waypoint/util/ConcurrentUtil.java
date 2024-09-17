package com.tomushimano.waypoint.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public final class ConcurrentUtil {
    private ConcurrentUtil() {
        throw new DontInvokeMe();
    }

    public static void terminate(ExecutorService executor, long timeoutSeconds) {
        try {
            // Attempt graceful shutdown
            if (!executor.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) executor.shutdownNow();
        } catch (InterruptedException ex) {
            // It failed, we're shutting it down for good
            executor.shutdownNow();
        }
    }
}
