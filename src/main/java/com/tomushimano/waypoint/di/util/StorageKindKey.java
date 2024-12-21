package com.tomushimano.waypoint.di.util;

import com.tomushimano.waypoint.datastore.StorageKind;
import dagger.MapKey;

@MapKey
public @interface StorageKindKey {

    StorageKind value();
}
