package com.tomushimano.waypoint.command.scaffold.bukkit;

import com.google.common.collect.ImmutableSet;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import grapefruit.command.runtime.dispatcher.CommandRegistrationHandler;
import grapefruit.command.runtime.dispatcher.tree.RouteNode;
import grapefruit.command.runtime.generated.CommandMirror;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.tomushimano.waypoint.util.ExceptionUtil.capture;

@Singleton
public final class CommandMapAccess implements CommandRegistrationHandler {
    private static final Logger LOGGER = NamespacedLoggerFactory.create(CommandMapAccess.class);
    private static final MethodHandle PLUGIN_COMMAND_FACTORY;
    private final Map<String, org.bukkit.command.Command> knownCommands = Bukkit.getCommandMap().getKnownCommands();
    private final BukkitCommandControl commandControl;
    private final JavaPlugin plugin;

    static {
        try {
            MethodHandles.Lookup caller = MethodHandles.lookup();
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(PluginCommand.class, caller);
            PLUGIN_COMMAND_FACTORY = lookup.findConstructor(PluginCommand.class, MethodType.methodType(void.class, String.class, Plugin.class));
        } catch (final ReflectiveOperationException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    @Inject
    public CommandMapAccess(BukkitCommandControl commandControl, JavaPlugin plugin) {
        this.commandControl = commandControl;
        this.plugin = plugin;
    }

    private Set<String> allAliases(RouteNode node) {
        return ImmutableSet.<String>builder()
                .add(node.primaryAlias())
                .addAll(node.secondaryAliases())
                .build();
    }

    @Override
    public boolean register(CommandMirror command) {
        RouteNode root = command.route().getFirst();

        String primaryAlias = root.primaryAlias();
        Set<String> secondaryAliases = root.secondaryAliases();

        unregisterIfExists(primaryAlias); // Unregister command with the primary alias, if it exists
        Set<String> allAliases = allAliases(root);

        try {
            // Create plugin command instance
            PluginCommand pluginCommand = createPluginCommand(primaryAlias, secondaryAliases, this.commandControl);
            // Store the constructed command instance in the bukkit command map
            for (String alias : allAliases) this.knownCommands.put(alias, pluginCommand);

            // Register aliases for tab-completion
            this.commandControl.track(allAliases);
        } catch (Throwable ex) {
            capture(ex, "Failed to register command with root aliases: %s".formatted(allAliases), LOGGER);
            // We don't want to proceed with the registration of this command any further.
            return false;
        }

        return true;
    }

    @Override
    public boolean unregister(CommandMirror command) {
        // Do nothing, we don't support the unregistering of commands right now.
        Set<String> allAliases = allAliases(command.route().getFirst());
        for (String alias : allAliases) this.knownCommands.remove(alias);

        // Remove tracked aliases
        this.commandControl.untrack(allAliases);

        // Always return true
        return true;
    }

    private PluginCommand createPluginCommand(String label, Set<String> aliases, CommandExecutor executor) throws Throwable {
        PluginCommand instance = (PluginCommand) PLUGIN_COMMAND_FACTORY.invoke(label, this.plugin);
        instance.setExecutor(executor);
        instance.setAliases(List.copyOf(aliases));
        return instance;
    }

    private void unregisterIfExists(String label) {
        org.bukkit.command.Command command = this.knownCommands.remove(label);
        if (command != null) command.getAliases().forEach(this.knownCommands::remove);
    }
}
