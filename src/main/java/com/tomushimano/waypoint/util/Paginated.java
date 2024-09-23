package com.tomushimano.waypoint.util;

import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

public class Paginated<T> {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private final List<T> items;
    private final Function<T, Component> itemFormatter;
    private final int pageSize;
    private final int page;
    private final int totalPages;

    private Paginated(List<T> items, Function<T, Component> itemFormatter, int pageSize, int page) {
        this.items = requireNonNull(items, "items cannot be null");
        this.itemFormatter = requireNonNull(itemFormatter, "lineFormatter cannot be null");
        checkArgument(pageSize >= 0, "pageSize cannot be negative");
        this.pageSize = pageSize;
        checkArgument(page >= 0, "page cannot be negative");
        this.page = page;
        int totalPages = items.size() / pageSize;
        if (items.size() % pageSize != 0) totalPages++;
        this.totalPages = totalPages;
    }

    public List<Component> viewPage() {
        int from = this.page * this.pageSize;

        return this.items.stream()
                .skip(Math.min(from, this.totalPages - 1))
                .limit(this.pageSize)
                .map(this.itemFormatter)
                .toList();
    }

    public int currentPage() {
        return this.page;
    }

    public int totalPages() {
        return this.totalPages;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static final class Builder<T> {
        private final List<T> items = new ArrayList<>();
        private Function<T, Component> itemFormatter;
        private int pageSize = DEFAULT_PAGE_SIZE;
        private int page = DEFAULT_PAGE;

        private Builder() {}

        public Builder<T> items(Collection<T> items) {
            this.items.addAll(items);
            return this;
        }

        public Builder<T> formatItem(Function<T, Component> itemFormatter) {
            this.itemFormatter = itemFormatter;
            return this;
        }

        public Builder<T> pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Builder<T> page(int page) {
            this.page = page;
            return this;
        }

        public Paginated<T> build() {
            return new Paginated<>(this.items, this.itemFormatter, this.pageSize, this.page);
        }
    }
}
