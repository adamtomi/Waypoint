package com.tomushimano.waypoint.core.hologram;

import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import com.tomushimano.waypoint.core.Waypoint;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.bukkit.Bukkit;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.function.Supplier;

import static com.tomushimano.waypoint.core.hologram.HologramLine.MARGIN;
import static com.tomushimano.waypoint.util.BukkitUtil.formatPosition;

@Singleton
public final class HologramFactory {
    private static final double OFFSET = 2.0D;
    private final MessageConfig messageConfig;

    @Inject
    public HologramFactory(MessageConfig messageConfig) {
        this.messageConfig = messageConfig;
    }

    public Hologram createHologram(Waypoint waypoint) {
        Supplier<Component> title = () -> Component.literal(waypoint.getName()).withColor(waypoint.getColor().value()).withStyle(ChatFormatting.BOLD);
        Supplier<Component> coordinates = () -> this.messageConfig.get(MessageKeys.Waypoint.HOLOGRAM_COORDINATES)
                .with(Placeholder.of("coordinates", formatPosition(waypoint.getPosition())))
                .makeNMS();
        Supplier<Component> owner = () -> this.messageConfig.get(MessageKeys.Waypoint.HOLOGRAM_OWNER)
                .with(Placeholder.of("owner", Bukkit.getOfflinePlayer(waypoint.getOwnerId()).getName()))
                .makeNMS();

        List<HologramLine> lines = List.of(
                HologramLine.create(title, () -> waypoint.getPosition().plus(0.0D, OFFSET, 0.0D)),
                HologramLine.empty(() -> waypoint.getPosition().plus(0.0D, OFFSET - MARGIN, 0.0D)),
                HologramLine.create(coordinates, () -> waypoint.getPosition().plus(0.0D, OFFSET - MARGIN * 2, 0.0D)),
                HologramLine.create(owner, () -> waypoint.getPosition().plus(0.0D, OFFSET - MARGIN * 3, 0.0D))
        );
        return new HologramImpl(lines);
    }
}
