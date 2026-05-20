package com.tomushimano.waypoint.core;

import com.tomushimano.waypoint.util.Position;
import org.bukkit.entity.Player;

public interface LightSource {

    void show(final Player player);

    void hide(final Player player);

    static LightSourceImpl at(final Position position) {
        return new LightSourceImpl(position);
    }
}
