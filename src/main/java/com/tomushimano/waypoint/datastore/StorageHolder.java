package com.tomushimano.waypoint.datastore;

import com.tomushimano.waypoint.util.Memoized;

public interface StorageHolder extends Memoized<Storage> {

    @Override
    Storage get();
}
