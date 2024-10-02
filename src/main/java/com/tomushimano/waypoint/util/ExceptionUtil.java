package com.tomushimano.waypoint.util;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.slf4j.Logger;

import java.util.concurrent.CompletionException;
import java.util.function.Function;

public final class ExceptionUtil {
    private ExceptionUtil() {
        throw new DontInvokeMe();
    }

    public static void capture(Throwable ex, String detail, Logger logger) {
        logger.error(">> An unexpected error has occurred, see below for details!");
        logger.error(detail, ex);
    }

    private static Throwable unwrap(Throwable ex) {
        if (ex instanceof CompletionException) return ex.getCause();
        return ex;
    }

    public static <T> Function<Throwable, T> capture(String detail, Logger logger) {
        return ex -> {
            capture(unwrap(ex), detail, logger);
            return null;
        };
    }

    public static <T> Function<Throwable, T> capture(CommandSender sender, Component message, String detail, Logger logger) {
        return ex -> {
            sender.sendMessage(message);
            capture(unwrap(ex), detail, logger);
            return null;
        };
    }
}
