package com.tomushimano.waypoint.util;

import java.util.function.IntPredicate;

public final class DoubleCheck {
    private DoubleCheck() {
        throw new DontInvokeMe();
    }

    public static int requireState(final int a, final IntPredicate condition, final String errorMessage) {
        if (!condition.test(a)) throw new IllegalStateException(errorMessage);
        return a;
    }

    public static int requirePositive(final int value, final String errorMessage) {
        return requireState(value, x -> x > 0, errorMessage);
    }
}
