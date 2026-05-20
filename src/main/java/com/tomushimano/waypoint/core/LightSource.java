package com.tomushimano.waypoint.core;

import com.tomushimano.waypoint.util.Position;

public interface LightSource extends WaypointElement {

    static LightSourceImpl at(final Position position) {
        return new LightSourceImpl(position);
    }
}
