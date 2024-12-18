package com.tomushimano.waypoint.util;

import org.bukkit.OfflinePlayer;

public final class BukkitUtil {
    private BukkitUtil() {
        throw new DontInvokeMe();
    }

    public static String formatPosition(final Position position) {
        return "%.3f;%.3f;%.3f".formatted(position.getX(), position.getY(), position.getZ());
    }

    public static String formatPlayer(final OfflinePlayer player) {
        return player.getName() +
                "/" +
                player.getUniqueId();
    }
}
