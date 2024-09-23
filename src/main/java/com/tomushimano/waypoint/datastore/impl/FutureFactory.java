package com.tomushimano.waypoint.datastore.impl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FutureFactory {
    private final ExecutorService executor = Executors.newCachedThreadPool(
            new ThreadFactoryBuilder().setNameFormat("waypoint-storage #%1$d").build()
    );

    public <T> CompletableFuture<T> futureOf(Callable<T> callable) {
        CompletableFuture<T> future = new CompletableFuture<>();
        this.executor.execute(() -> {
            try {
                future.complete(callable.call());
            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
        });

        return future;
    }

    public CompletableFuture<?> futureOf(VoidCallable callable) {
        CompletableFuture<?> future = new CompletableFuture<>();
        this.executor.execute(() -> {
            try {
                callable.call();
                future.complete(null);
            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
        });

        return future;
    }

    @FunctionalInterface
    public interface VoidCallable {

        void call() throws Exception;
    }
}
