package com.tomushimano.waypoint.command.scaffold;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.tomushimano.waypoint.config.ConfigKey;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.di.qualifier.Cmd;
import com.tomushimano.waypoint.di.qualifier.Lang;
import com.tomushimano.waypoint.message.Messages;
import grapefruit.command.argument.CommandArgument;
import grapefruit.command.argument.CommandChain;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.dispatcher.ExecutionListener;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.time.Duration;
import java.util.UUID;
import java.util.stream.Collectors;

public class ConfirmationHandler implements ExecutionListener.Pre<CommandSender> {
    private static final String CONFIG_KEY = "__confirmation__";
    private final Cache<UUID, CommandChain<CommandSender>> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(10L))
            .weakValues()
            .build();
    private final Configurable config;
    private final Configurable messageConfig;

    @Inject
    public ConfirmationHandler(final @Cmd Configurable config, final @Lang Configurable messageConfig) {
        this.config = config;
        this.messageConfig = messageConfig;
    }

    @Override
    public boolean invoke(final CommandContext<CommandSender> context) {
        final CommandChain<CommandSender> chain = context.chain();
        final ConfigKey<Boolean> key = buildConfigKey(chain);
        if (!this.config.get(key)) return true; // Confirmation was not enabled for this command, return

        final CommandSender sender = context.source();
        final UUID senderId = sender instanceof Player player
                ? player.getUniqueId()
                : Console.ID;

        final CommandChain<CommandSender> cachedChain = this.cache.getIfPresent(senderId);
        if (cachedChain == null) {
            Messages.COMMAND__CONFIRMATION_REQUIRED.from(this.messageConfig).print(sender);
            this.cache.put(senderId, chain);
            return false;
        }

        this.cache.invalidate(senderId);
        return true;
    }

    private ConfigKey<Boolean> buildConfigKey(final CommandChain<CommandSender> chain) {
        return ConfigKey.boolKey("%s.%s".formatted(buildCommandPath(chain), CONFIG_KEY));
    }

    private String buildCommandPath(final CommandChain<CommandSender> chain) {
        return chain.route().stream()
                .skip(1L) // Skip root command name (waypoint/waypointadmin)
                .map(CommandArgument::name)
                .collect(Collectors.joining("."));
    }
}
