package com.tomushimano.waypoint.datastore.impl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tomushimano.waypoint.util.ConcurrentUtil;

import javax.inject.Inject;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FutureFactory implements AutoCloseable {
    private final ExecutorService executor = Executors.newCachedThreadPool(
            new ThreadFactoryBuilder().setNameFormat("waypoint-storage #%1$d").build()
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

    @Override
    public void close() {
        ConcurrentUtil.terminate(this.executor, 1L);
    }

    @FunctionalInterface
    public interface VoidCallable {

        void call() throws Exception;
    }
}
