package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.command.scaffold.CommandHelper;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.util.ParticleStream;
import grapefruit.command.CommandModule;
import grapefruit.command.argument.CommandChain;
import grapefruit.command.argument.CommandChainFactory;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.util.key.Key;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static grapefruit.command.argument.condition.CommandCondition.and;

public class StartNavigationCommand implements CommandModule<CommandSender> {
    private static final Key<Waypoint> DESTINATION_KEY = Key.named(Waypoint.class, "destination");
    private static final Key<Boolean> FORCE_KEY = Key.named(Boolean.class, "force");
    // TODO remove this
    private static final Map<UUID, ParticleStream> runningParticles = new ConcurrentHashMap<>();
    private final CommandHelper helper;

    @Inject
    public StartNavigationCommand(final CommandHelper helper) {
        this.helper = helper;
    }

    @Override
    public CommandChain<CommandSender> chain(final CommandChainFactory<CommandSender> factory) {
        return factory.newChain()
                .then(factory.literal("waypoint").aliases("wp").build())
                .then(factory.literal("navigation").aliases("nav").expect(and(
                        this.helper.perm("waypoint.navigation"), this.helper.isPlayer()
                )).build())
                .then(factory.literal("start").aliases("begin").build())
                .arguments()
                .then(factory.required(DESTINATION_KEY).mapWith(this.helper.stdWaypoint()).build())
                .flags()
                .then(factory.presenceFlag(FORCE_KEY).assumeShorthand().build())
                .build();
    }

    @Override
    public void execute(final CommandContext<CommandSender> context) {
        final Player sender = (Player) context.source();
        final Waypoint waypoint = context.require(DESTINATION_KEY);

        final Location origin = sender.getLocation();
        final Location destination = waypoint.getPosition().toLocation();

        final ParticleStream existing = runningParticles.get(sender.getUniqueId());
        if (existing != null) {
            if (!context.has(FORCE_KEY)) {
                sender.sendMessage("You already have a particle stream running.");
                return;
            } else {
                sender.sendMessage("Cancelling previous particle stream...");
                existing.cancel();
                runningParticles.remove(sender.getUniqueId());
            }
        }

        sender.sendMessage("Starting new particle stream...");
        final ParticleStream stream = ParticleStream.create(origin, destination);
        runningParticles.put(sender.getUniqueId(), stream);
        stream.play(sender, 15);

        sender.sendMessage("Particle stream is over, removing it...");
        runningParticles.remove(sender.getUniqueId(), stream);
    }
}
