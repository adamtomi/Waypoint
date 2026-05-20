package com.tomushimano.waypoint.command.impl;

import grapefruit.command.CommandModule;
import grapefruit.command.argument.CommandChain;
import grapefruit.command.argument.CommandChainFactory;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.util.key.Key;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.impl.CraftLight;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import javax.inject.Inject;

import static com.tomushimano.waypoint.command.scaffold.mapper.IntArgumentMapper.positiveInt;

public class PlaceLightCommand implements CommandModule<CommandSender> {
    private static final Key<Integer> LEVEL_KEY = Key.named(Integer.class, "level");

    @Inject
    public PlaceLightCommand() {}

    @Override
    public CommandChain<CommandSender> chain(final CommandChainFactory<CommandSender> factory) {
        return factory.newChain()
                .then(factory.literal("placelight").build())
                .arguments()
                .then(factory.required(LEVEL_KEY).mapWith(positiveInt()).build())
                .build();
    }

    @Override
    public void execute(final CommandContext<CommandSender> context) {
        final Player player = (Player) context.source();
        final int level = context.require(LEVEL_KEY);

        final CraftBlock block = (CraftBlock) player.getWorld().getBlockAt(player.getLocation());
        if (!block.getType().equals(Material.AIR)) {
            player.sendMessage("Light cannot be placed inisde blocks.");
            return;
        }

        final CraftLight blockData = (CraftLight) Material.LIGHT.createBlockData();
        blockData.setLevel(level);

        final ClientboundBlockUpdatePacket packet = new ClientboundBlockUpdatePacket(block.getPosition(), blockData.getState());
        ((CraftPlayer) player).getHandle().connection.send(packet);
        player.sendMessage("Light updated to level %s".formatted(level));
    }
}
