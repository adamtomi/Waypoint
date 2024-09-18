package com.tomushimano.waypoint.util;

import org.bukkit.entity.Player;

public final class BukkitUtil {
    private BukkitUtil() {
        throw new DontInvokeMe();
    }

    public static String formatPosition(Position position) {
        return "%.3f;%.3f;%.3f".formatted(position.getX(), position.getY(), position.getZ());
    }

    public static String formatPlayer(Player player) {
        return player.getName() +
                "/" +
                player.getUniqueId();
    }
}
