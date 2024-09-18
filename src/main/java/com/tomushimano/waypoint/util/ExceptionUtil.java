package com.tomushimano.waypoint.util;

import org.bukkit.command.CommandSender;
import org.slf4j.Logger;

import java.util.function.Function;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public final class ExceptionUtil {
    private ExceptionUtil() {
        throw new DontInvokeMe();
    }

    public static void capture(Throwable ex, String detail, Logger logger) {
        logger.error("");
        logger.error(">> An unexpected error has occurred, see below for details!");
        logger.error(detail, ex);
        logger.error("");
    }

    public static <T> Function<Throwable, T> capture(String detail, Logger logger) {
        return ex -> {
            capture(ex, detail, logger);
            return null;
        };
    }

    public static <T> Function<Throwable, T> capture(CommandSender sender, String detail, Logger logger) {
        sender.sendMessage(text("Database operation failed.", RED));
        return capture(detail, logger);
    }
}
