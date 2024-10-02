package com.tomushimano.waypoint.core.hologram;

import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.util.Position;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.bukkit.Bukkit;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static com.tomushimano.waypoint.util.BukkitUtil.formatPosition;

@Singleton
public class HologramFactory {
    private final MessageConfig messageConfig;

    @Inject
    public HologramFactory(MessageConfig messageConfig) {
        this.messageConfig = messageConfig;
    }

    public Hologram createHologram(Waypoint waypoint) {
        Component title = Component.literal(waypoint.getName()).withColor(waypoint.getColor().value()).withStyle(ChatFormatting.BOLD);
        Component coordinates = Component.literal(this.messageConfig.get(MessageKeys.Waypoint.HOLOGRAM_COORDINATES)
                .with(Placeholder.of("coordinates", formatPosition(waypoint.getPosition())))
                .makeString().replace("&", "§"));
        Component owner = Component.literal(this.messageConfig.get(MessageKeys.Waypoint.HOLOGRAM_OWNER)
                .with(Placeholder.of("owner", Bukkit.getOfflinePlayer(waypoint.getOwnerId()).getName()))
                .makeString().replace("&", "§"));
        Position reference = waypoint.getPosition().plus(0.0D, 2.0D, 0.0D);
        double offset = 0.0D;

        List<HologramLine> lines = List.of(
                HologramLine.create(title, reference),
                HologramLine.empty(reference.plus(0.0D, (offset = offset -0.3D), 0.0D)),
                HologramLine.create(coordinates, reference.plus(0.0D, (offset = offset -0.3D), 0.0D)),
                HologramLine.create(owner, reference.plus(0.0D, offset, 0.0D))
        );
        return new HologramImpl(lines);
    }
}
