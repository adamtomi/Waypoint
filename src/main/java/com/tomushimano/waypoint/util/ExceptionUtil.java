package com.tomushimano.waypoint.util;

import org.slf4j.Logger;

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
}
