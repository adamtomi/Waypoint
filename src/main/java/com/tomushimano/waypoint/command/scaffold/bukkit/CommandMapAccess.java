package com.tomushimano.waypoint.command.scaffold.bukkit;

import com.google.common.collect.ImmutableSet;
import com.tomushimano.waypoint.command.CommandManager;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import grapefruit.command.argument.CommandArgument;
import grapefruit.command.argument.CommandChain;
import grapefruit.command.dispatcher.CommandRegistrationHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.tomushimano.waypoint.util.ExceptionUtil.capture;

public final class CommandMapAccess implements CommandRegistrationHandler<CommandSender> {
    private static final Logger LOGGER = NamespacedLoggerFactory.create(CommandMapAccess.class);
    private static final MethodHandle PLUGIN_COMMAND_FACTORY;
    private final Map<String, Command> knownCommands = Bukkit.getCommandMap().getKnownCommands();
    private final CommandManager commandManager;
    private final JavaPlugin plugin;

    static {
        try {
            final MethodHandles.Lookup caller = MethodHandles.lookup();
            final MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(PluginCommand.class, caller);
            PLUGIN_COMMAND_FACTORY = lookup.findConstructor(PluginCommand.class, MethodType.methodType(void.class, String.class, Plugin.class));
        } catch (final ReflectiveOperationException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    @AssistedInject
    public CommandMapAccess(final @Assisted CommandManager commandManager, final JavaPlugin plugin) {
        this.commandManager = commandManager;
        this.plugin = plugin;
    }

    private Set<String> allAliases(final CommandArgument.Literal<CommandSender> node) {
        return ImmutableSet.<String>builder()
                .add(node.name())
                .addAll(node.aliases())
                .build();
    }

    @Override
    public boolean register(final CommandChain<CommandSender> chain) {
        final CommandArgument.Literal<CommandSender> root = chain.route().getFirst();

        final String primaryAlias = root.name();
        final Set<String> secondaryAliases = root.aliases();

        unregisterIfExists(primaryAlias); // Unregister command with the primary alias, if it exists
        final Set<String> allAliases = allAliases(root);

        try {
            // Create plugin command instance
            final PluginCommand pluginCommand = createPluginCommand(primaryAlias, secondaryAliases, this.commandManager);
            // Store the constructed command instance in the bukkit command map
            for (final String alias : allAliases) this.knownCommands.put(alias, pluginCommand);

            // Register aliases for tab-completion
            this.commandManager.track(allAliases);
        } catch (final Throwable ex) {
            capture(ex, "Failed to register command with root aliases: %s".formatted(allAliases), LOGGER);
            // We don't want to proceed with the registration of this command any further.
            return false;
        }

        return true;
    }

    @Override
    public boolean unregister(final CommandChain<CommandSender> chain) {
        // Do nothing, we don't support the unregistering of commands right now.
        final Set<String> allAliases = allAliases(chain.route().getFirst());
        for (final String alias : allAliases) this.knownCommands.remove(alias);

        // Remove tracked aliases
        this.commandManager.untrack(allAliases);

        // Always return true
        return true;
    }

    private PluginCommand createPluginCommand(final String label, final Set<String> aliases, final CommandExecutor executor) throws Throwable {
        final PluginCommand instance = (PluginCommand) PLUGIN_COMMAND_FACTORY.invoke(label, this.plugin);
        instance.setExecutor(executor);
        instance.setAliases(List.copyOf(aliases));
        return instance;
    }

    private void unregisterIfExists(final String label) {
        final Command command = this.knownCommands.remove(label);
        if (command != null) command.getAliases().forEach(this.knownCommands::remove);
    }

    @AssistedFactory
    public interface Factory {

        CommandMapAccess create(final CommandManager commandManager);
    }
}
