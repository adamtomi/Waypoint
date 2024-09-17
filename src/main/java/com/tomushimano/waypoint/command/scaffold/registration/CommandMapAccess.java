package com.tomushimano.waypoint.command.scaffold.registration;

import com.google.common.collect.ImmutableSet;
import com.tomushimano.waypoint.command.CommandManager;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import grapefruit.command.dispatcher.CommandRegistrationHandler;
import grapefruit.command.dispatcher.tree.RouteNode;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.tomushimano.waypoint.util.ExceptionUtil.capture;
import static java.util.Objects.requireNonNull;

@SuppressWarnings("unchecked")
public final class CommandMapAccess extends CommandRegistrationHandler {
    private static final Logger LOGGER = NamespacedLoggerFactory.create("CommandMapAccess");
    private static final CommandMap COMMAND_MAP = Bukkit.getCommandMap();
    private static final Constructor<PluginCommand> PLUGIN_COMMAND_CONSTRUCTOR;
    private static final Map<String, org.bukkit.command.Command> KNOWN_COMMANDS;
    private final CommandManager commandManager;
    private final JavaPlugin plugin;

    static {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            PLUGIN_COMMAND_CONSTRUCTOR = constructor;

            Field knownCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommands.setAccessible(true);
            KNOWN_COMMANDS = (Map<String, org.bukkit.command.Command>) knownCommands.get(COMMAND_MAP);
        } catch (final ReflectiveOperationException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public CommandMapAccess(CommandManager commandManager, JavaPlugin plugin) {
        this.commandManager = requireNonNull(commandManager, "commandManager cannot be null");
        this.plugin = requireNonNull(plugin, "plugin cannot be null");
    }

    @Override
    public void onRegister(grapefruit.command.Command command) {
        RouteNode root = command.spec().route().getFirst();

        String primaryAlias = root.primaryAlias();
        Set<String> secondaryAliases = root.secondaryAliases();

        unregisterIfExists(primaryAlias); // Unregister command with the primary alias, if it exists
        Set<String> allAliases = ImmutableSet.<String>builder()
                .add(primaryAlias)
                .addAll(secondaryAliases)
                .build();

        try {
            // Create plugin command instance
            PluginCommand pluginCommand = createPluginCommand(primaryAlias, secondaryAliases, this.commandManager);
            // Store the constructed command instance in the bukkit command map
            for (String alias : allAliases) KNOWN_COMMANDS.put(alias, pluginCommand);

            // Register aliases for tab-completion
            this.commandManager.track(allAliases);
        } catch (ReflectiveOperationException ex) {
            capture(ex, "Failed to register command with root aliases: %s".formatted(allAliases), LOGGER);
            // We don't want to proceed with the registration of this command any further.
            interrupt();
        }
    }

    @Override
    public void onUnregister(grapefruit.command.Command command) {
        // Do nothing, we don't support the unregistering of commands right now.
    }

    private PluginCommand createPluginCommand(String label, Set<String> aliases, CommandExecutor executor) throws ReflectiveOperationException {
        PluginCommand instance = PLUGIN_COMMAND_CONSTRUCTOR.newInstance(label, this.plugin);
        instance.setExecutor(executor);
        instance.setAliases(List.copyOf(aliases));
        return instance;
    }

    private void unregisterIfExists(String label) {
        org.bukkit.command.Command command = KNOWN_COMMANDS.remove(label);
        if (command != null) command.getAliases().forEach(KNOWN_COMMANDS::remove);
    }
}
