package com.tomushimano.waypoint.util;

import java.util.List;

import static com.tomushimano.waypoint.util.DoubleCheck.requireState;
import static java.util.Objects.requireNonNull;

public class Paginator<T> {
    private static final int DEFAULT_PAGE_SIZE = 12;
    private final List<T> items;
    private final int pageSize;
    private final int total;

    private Paginator(final List<T> items, final int pageSize, final int total) {
        this.items = requireNonNull(items, "items cannot be null");
        this.pageSize = requireState(pageSize, x -> x > 0, "page size must be greater than zero");
        this.total = requireState(total, x -> x >= 0, "total cannot be negative");
    }

    public static <T> Paginator<T> create(final List<T> items) {
        final int pageSize = DEFAULT_PAGE_SIZE;
        int total = items.size() / pageSize;
        if (items.size() % pageSize != 0) total++;

        return new Paginator<>(items, pageSize, total);
    }

    public int normalize(final int page) {
        if (page < 0) {
            return 0;
        } else if (page >= this.total) {
            return this.total - 1;
        }

        return page;
    }

    public List<T> page(final int page) {
        final int from = normalize(page) * this.pageSize;

        return this.items.stream()
                .skip(Math.min(from, this.total - 1))
                .limit(this.pageSize)
                .toList();
    }

    public int total() {
        return this.total;
    }
}
