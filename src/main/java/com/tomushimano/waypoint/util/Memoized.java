package com.tomushimano.waypoint.util;

import java.util.function.Supplier;

public interface Memoized<T> {

    T get();

    static <T> Memoized<T> of(Supplier<T> factory) {
        return new Memoized<>() {
            private T value;

            @Override
            public T get() {
                if (this.value == null) {
                    this.value = factory.get();
                    if (this.value == null) throw new IllegalStateException("Received null from the value factory");
                }

                return this.value;
            }
        };
    }
}
