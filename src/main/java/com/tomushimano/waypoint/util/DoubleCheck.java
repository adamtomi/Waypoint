package com.tomushimano.waypoint.util;

import java.util.function.IntPredicate;

public final class DoubleCheck {
    private DoubleCheck() {
        throw new DontInvokeMe();
    }

    public static int requireState(int a, IntPredicate condition, String errorMessage) {
        if (!condition.test(a)) throw new IllegalStateException(errorMessage);
        return a;
    }
}
