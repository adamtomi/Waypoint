package com.tomushimano.waypoint.core.hologram;

import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.config.StandardKeys;
import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.di.qualifier.Cfg;
import com.tomushimano.waypoint.util.Position;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.bukkit.Bukkit;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.tomushimano.waypoint.util.BukkitUtil.formatPosition;

@Singleton
public final class HologramFactory {
    private final MessageConfig messageConfig;
    private final Configurable config;

    @Inject
    public HologramFactory(final MessageConfig messageConfig, final @Cfg Configurable config) {
        this.messageConfig = messageConfig;
        this.config = config;
    }

    public Hologram createHologram(final Waypoint waypoint) {
        final Supplier<Component> title = () -> Component.literal(waypoint.getName()).withColor(waypoint.getColor().value()).withStyle(ChatFormatting.BOLD);
        final Supplier<Component> coordinates = () -> this.messageConfig.get(MessageKeys.Waypoint.HOLOGRAM_COORDINATES)
                .with(Placeholder.of("coordinates", formatPosition(waypoint.getPosition())))
                .makeNMS();
        final Supplier<Component> owner = () -> this.messageConfig.get(MessageKeys.Waypoint.HOLOGRAM_OWNER)
                .with(Placeholder.of("owner", Bukkit.getOfflinePlayer(waypoint.getOwnerId()).getName()))
                .makeNMS();

        final Function<Integer, Supplier<Position>> positionFactory = lineIdx -> () -> waypoint.getPosition().plus(
                0.0D,
                this.config.get(StandardKeys.Hologram.TOP_OFFSET) - this.config.get(StandardKeys.Hologram.LINE_PADDING) * lineIdx,
                0.0D
        );

        final List<HologramLine> lines = List.of(
                HologramLine.create(title, positionFactory.apply(0)),
                HologramLine.empty(positionFactory.apply(1)),
                HologramLine.create(coordinates, positionFactory.apply(2)),
                HologramLine.create(owner, positionFactory.apply(3))
        );
        return new HologramImpl(lines);
    }
}
