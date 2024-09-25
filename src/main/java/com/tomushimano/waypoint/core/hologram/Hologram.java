package com.tomushimano.waypoint.core.hologram;

import com.tomushimano.waypoint.core.Waypoint;

public interface Hologram {

    void spawn();

    void despawn();

    static Hologram create(Waypoint waypoint) {
        return new HologramImpl(waypoint);
    }
}
