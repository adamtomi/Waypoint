package com.tomushimano.waypoint.datastore;

import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.config.StandardKeys;
import com.tomushimano.waypoint.di.qualifier.Cfg;
import com.tomushimano.waypoint.util.Memoized;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@Singleton
public class StorageHolderImpl implements StorageHolder {
    private final Memoized<Storage> implHolder = Memoized.of(this::compute);
    private final Configurable config;
    private final Map<StorageKind, Storage> storageImpls;

    @Inject
    public StorageHolderImpl(final @Cfg Configurable config, final Map<StorageKind, Storage> storageImpls) {
        this.config = config;
        this.storageImpls = storageImpls;
    }

    @Override
    public Storage get() {
        return this.implHolder.get();
    }

    private Storage compute() {
        final StorageKind storageKind = this.config.get(StandardKeys.Database.TYPE);
        if (!this.storageImpls.containsKey(storageKind)) {
            throw new UnsupportedOperationException("Storage kind '%s' is not supported currently".formatted(storageKind));
        }

        final Storage impl = this.storageImpls.get(storageKind);
        return requireNonNull(impl, "impl cannot be null");
    }
}
