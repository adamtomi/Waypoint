package com.tomushimano.waypoint.core.hologram;

import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.config.StandardKeys;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.di.qualifier.Cfg;
import com.tomushimano.waypoint.di.qualifier.Lang;
import com.tomushimano.waypoint.message.Messages;
import com.tomushimano.waypoint.util.Position;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Singleton
public final class HologramFactory {
    private final Configurable config;
    private final Configurable langConfig;

    @Inject
    public HologramFactory(final @Cfg Configurable config, final @Lang Configurable langConfig) {
        this.config = config;
        this.langConfig = langConfig;
    }

    public Hologram createHologram(final Waypoint waypoint) {
        final OfflinePlayer owner = Bukkit.getOfflinePlayer(waypoint.getOwnerId());
        final Supplier<Component> titleLine = () -> Component.literal(waypoint.getName()).withColor(waypoint.getColor().value()).withStyle(ChatFormatting.BOLD);
        final Supplier<Component> coordsLine = () -> Messages.WAYPOINT__HOLOGRAM_COORDINATES.from(this.langConfig, waypoint).nms();
        final Supplier<Component> ownerLine = () -> Messages.WAYPOINT__HOLOGRAM_OWNER.from(this.langConfig, owner).nms();

        final Function<Integer, Supplier<Position>> positionFactory = lineIdx -> () -> waypoint.getPosition().plus(
                0.0D,
                this.config.get(StandardKeys.Hologram.TOP_OFFSET) - this.config.get(StandardKeys.Hologram.LINE_PADDING) * lineIdx,
                0.0D
        );

        final List<HologramLine> lines = List.of(
                HologramLine.create(titleLine, positionFactory.apply(0)),
                HologramLine.empty(positionFactory.apply(1)),
                HologramLine.create(coordsLine, positionFactory.apply(2)),
                HologramLine.create(ownerLine, positionFactory.apply(3))
        );
        return new HologramImpl(lines);
    }
}
