package com.tomushimano.waypoint.core.lifecycle;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Singleton
public class LifecycleService implements LifecycleAware {
    private static final Comparator<Entry> DEFAULT_COMPARATOR = Comparator.comparingInt(Entry::priority);
    private static final int DEFAULT_PRIORITY = 1;
    private final List<Entry> registrations = new ArrayList<>();
    private boolean sealed = false;

    @Inject
    public LifecycleService() {}

    public void register(LifecycleAware entry, int priority) {
        if (this.sealed) throw new IllegalStateException("Service is sealed, registrations are not allowed in this state");
        if (entry.equals(this)) {
            throw new IllegalArgumentException("Cannot register lifecycle service to itself");
        }

        this.registrations.add(new Entry(entry, priority));
    }

    public void register(LifecycleAware entry) {
        register(entry, DEFAULT_PRIORITY);
    }

    @Override
    public void setup() {
        this.sealed = true;
        this.registrations.sort(DEFAULT_COMPARATOR);
        for (Entry entry : this.registrations) entry.entry().setup();
    }

    @Override
    public void teardown() {
        for (Entry entry : this.registrations) entry.entry().teardown();
    }

    private record Entry(LifecycleAware entry, int priority) {}
}
