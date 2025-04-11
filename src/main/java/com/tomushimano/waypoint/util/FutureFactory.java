package com.tomushimano.waypoint.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Singleton
public final class FutureFactory {
    private final ExecutorService executor = Executors.newCachedThreadPool(
            new ThreadFactoryBuilder().setNameFormat("waypoint-async-worker #%1$d").build()
    );

    @Inject
    public FutureFactory() {}

    public <T> CompletableFuture<T> futureOf(final Callable<T> callable) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        this.executor.execute(() -> {
            try {
                future.complete(callable.call());
            } catch (final Exception ex) {
                future.completeExceptionally(ex);
            }
        });

        return future;
    }

    public CompletableFuture<?> futureOf(final VoidCallable callable) {
        final CompletableFuture<?> future = new CompletableFuture<>();
        this.executor.execute(() -> {
            try {
                callable.call();
                future.complete(null);
            } catch (final Exception ex) {
                future.completeExceptionally(ex);
            }
        });

        return future;
    }

    public void shutdown(long timeoutSeconds) {
        try {
            // Attempt graceful shutdown
            if (!this.executor.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) this.executor.shutdownNow();
        } catch (final InterruptedException ex) {
            // It failed, we're shutting it down for good
            this.executor.shutdownNow();
        }
    }

    @FunctionalInterface
    public interface VoidCallable {

        void call() throws Exception;
    }
}
