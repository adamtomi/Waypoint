package com.tomushimano.waypoint.util;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.slf4j.Logger;

import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ExceptionUtil {
    private ExceptionUtil() {
        throw new DontInvokeMe();
    }

    public static void capture(final Throwable ex, final String detail, final Logger logger) {
        logger.error(">> An unexpected error has occurred, see below for details!");
        logger.error(detail, ex);
    }

    private static Throwable unwrap(final Throwable ex) {
        if (ex instanceof CompletionException) return ex.getCause();
        return ex;
    }

    public static <T> Function<Throwable, T> capture(final String detail, final Logger logger) {
        return ex -> {
            capture(unwrap(ex), detail, logger);
            return null;
        };
    }

    @Deprecated
    public static <T> Function<Throwable, T> capture(
            final CommandSender sender,
            final Component message,
            final String detail,
            final Logger logger
    ) {
        return ex -> {
            sender.sendMessage(message);
            capture(unwrap(ex), detail, logger);
            return null;
        };
    }

    public static <T> Function<Throwable, T> capture(
            final Runnable action,
            final String detail,
            final Logger logger
    ) {
        return ex -> {
            action.run();
            capture(unwrap(ex), detail, logger);
            return null;
        };
    }
}
