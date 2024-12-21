package com.tomushimano.waypoint.core.hologram;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * Holograms are composed of multiple {@link HologramLine lines}.
 */
public interface Hologram {

    /**
     * Returns the {@link HologramLine lines} associated with this
     * hologram.
     *
     * @return The linest
     */
    List<HologramLine> lines();

    /**
     * Show the entire hologram to the player.
     *
     * @param player The player
     */
    default void show(final Player player) {
        lines().forEach(line -> line.spawnPacket().send(player));
    }

    /**
     * Hide the entire hologram from the player.
     *
     * @param player The player
     */
    default void hide(final Player player) {
        lines().forEach(line -> line.despawnPacket().send(player));
    }
}
