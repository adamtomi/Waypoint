package com.tomushimano.waypoint.util;

@FunctionalInterface
public interface TriFunction<A, B, C, D> {

    D apply(A a, B b, C c);
}