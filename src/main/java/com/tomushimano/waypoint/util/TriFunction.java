package com.tomushimano.waypoint.util;

@FunctionalInterface
public interface TriFunction<A, B, C, D> {

    D apply(final A a, final B b, final C c);
}
