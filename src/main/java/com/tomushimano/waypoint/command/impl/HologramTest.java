package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.command.scaffold.VerboseArgumentMappingException;
import com.tomushimano.waypoint.core.hologram.HologramLine;
import com.tomushimano.waypoint.util.Position;
import grapefruit.command.CommandModule;
import grapefruit.command.argument.CommandChain;
import grapefruit.command.argument.CommandChainFactory;
import grapefruit.command.argument.mapper.ArgumentMapper;
import grapefruit.command.argument.mapper.builtin.NumericArgumentMapper;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.util.key.Key;
import net.minecraft.network.chat.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static com.tomushimano.waypoint.command.scaffold.condition.IsPlayerCondition.isPlayer;
import static net.kyori.adventure.text.Component.text;

public class HologramTest implements CommandModule<CommandSender> {
    private static final Map<UUID, HologramLine> HOLOGRAM_CACHE = new ConcurrentHashMap<>();
    private static final Key<Long> ID_KEY = Key.named(Long.class, "id");
    private static final Key<Boolean> DESPAWN_KEY = Key.named(Boolean.class, "despawn");
    private static final Function<Position, HologramLine> HOLOGRAM_FACTORY = it -> HologramLine.create(
            () -> Component.literal("Hello, this is a test"),
            () -> it
    );

    private static ArgumentMapper<CommandSender, Long> longMapper() {
        return NumericArgumentMapper.longMapper(() -> new VerboseArgumentMappingException(__ -> text("Invalid long value provided.")));
    }

    @Inject
    public HologramTest() {}

    @Override
    public CommandChain<CommandSender> chain(final CommandChainFactory<CommandSender> factory) {
        return factory.newChain()
                .then(factory.literal("hologramtest").aliases("htest").expect(isPlayer()).build())
                .arguments()
                .then(factory.required(ID_KEY).mapWith(longMapper()).build())
                .flags()
                .then(factory.boolFlag(DESPAWN_KEY).assumeShorthand().build())
                .build();
    }

    @Override
    public void execute(final CommandContext<CommandSender> context) {
        final Player player = (Player) context.source();
        final long id = context.require(ID_KEY);
        final boolean despawn = context.has(DESPAWN_KEY);
        final HologramLine line = HOLOGRAM_CACHE.computeIfAbsent(player.getUniqueId(), __ -> HOLOGRAM_FACTORY.apply(Position.from(player.getLocation())));

        if (despawn) {
            line.despawnPacket().send(player);
            player.sendMessage("Hologram has been despawned.");
        } else {
            line.spawnPacket().send(player);
            player.sendMessage("Hologram has been spawned.");
        }
    }
}
