package com.tomushimano.waypoint.util;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.Nullable;

public final class BukkitUtil {
    private BukkitUtil() {
        throw new DontInvokeMe();
    }

    public static String formatPlayer(final OfflinePlayer player) {
        return player.getName() +
                "/" +
                player.getUniqueId();
    }

    public static int getSensibleHighestY(final World world, final double x, final double z, final Player player) {
        final int highestY = world.getHighestBlockYAt((int) x, (int) z);
        final int currentY = player.getLocation().getBlockY();

        return Math.min(highestY, currentY);
    }

    public static double distanceIgnoreY(final Entity entity, final Location destination) {
        final Location origin = entity.getLocation();
        final double sum = NumberConversions.square(origin.getX() - destination.getX()) + NumberConversions.square(origin.getZ() - destination.getZ());
        return Math.sqrt(sum);
    }

    public static @Nullable String getColorName(final TextColor color) {
        return color instanceof NamedTextColor named
                ? named.toString()
                : null;
    }
}
