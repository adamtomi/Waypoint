package com.tomushimano.waypoint.command;

import com.tomushimano.waypoint.command.scaffold.CommandHolder;
import com.tomushimano.waypoint.command.scaffold.Owning;
import com.tomushimano.waypoint.command.scaffold.Sender;
import com.tomushimano.waypoint.command.scaffold.condition.IsPlayer;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.core.WaypointService;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import grapefruit.command.CommandContainer;
import grapefruit.command.annotation.CommandDefinition;
import grapefruit.command.annotation.argument.Arg;
import grapefruit.command.annotation.argument.Flag;
import grapefruit.command.dispatcher.CommandDispatcher;
import org.bukkit.entity.Player;
import org.slf4j.Logger;

import javax.inject.Inject;

import java.util.Set;

import static com.tomushimano.waypoint.util.BukkitUtil.formatPosition;
import static com.tomushimano.waypoint.util.ExceptionUtil.capture;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;

public class WaypointCommands implements CommandHolder {
    private static final Logger LOGGER = NamespacedLoggerFactory.create("Commands");
    private final CommandContainer container = new WaypointCommands_Container(this);
    private final WaypointService waypointService;

    @Inject
    public WaypointCommands(WaypointService waypointService) {
        this.waypointService = waypointService;
    }

    @Override
    public void registerCommands(CommandDispatcher dispatcher) {
        dispatcher.register(this.container);
    }

    @CommandDefinition(route = "waypoint|wp set", permission = "waypoint.set", conditions = { IsPlayer.class })
    public void set(@Sender Player sender, @Arg String name, @Flag boolean global) {
        this.waypointService.createWaypoint(name, null, global)
                .thenApply(Waypoint::getPosition)
                .thenApply(x -> text("Your waypoint has been created in %s at %s".formatted(x.getWorldName(), formatPosition(x)), GREEN))
                .thenAccept(sender::sendMessage)
                .exceptionally(capture(sender, "Failed to create waypoint", LOGGER));
    }

    @CommandDefinition(route = "waypoint|wp remove|rm", permission = "waypoint.remove", conditions = { IsPlayer.class })
    public void remove(@Sender Player sender, @Arg @Owning Waypoint waypoint) {
        this.waypointService.removeWaypoint(waypoint)
                .thenApply(x -> text("Waypoint '%s' has been deleted.".formatted(waypoint.getName()), GREEN))
                .thenAccept(sender::sendMessage)
                .exceptionally(capture(sender, "Failed to remove waypoint", LOGGER));
    }

    @CommandDefinition(route = "waypoint|wp list|ls", permission = "waypoint.list", conditions = { IsPlayer.class })
    public void list(@Sender Player sender, @Flag boolean hideGlobal) {
        Set<Waypoint> waypoints = hideGlobal
                ? this.waypointService.getOwnedWaypoints(sender)
                : this.waypointService.getAccessibleWaypoints(sender);
    }

    @CommandDefinition(route = "waypoint|wp toggle", permission = "waypoint.toggle", conditions = { IsPlayer.class })
    public void toggle(@Sender Player sender, @Flag Waypoint waypoint) {

    }

    @CommandDefinition(route = "waypoint|wp edit", permission = "waypoint.edit", conditions = { IsPlayer.class })
    public void edit(@Sender Player sender, @Arg @Owning  Waypoint waypoint, @Flag String name, @Flag boolean toggleGlobal) {

    }

    @CommandDefinition(route = "waypoint|wp reloc|movehere", permission = "waypoint.reloc", conditions = { IsPlayer.class })
    public void reloc(@Sender Player sender, @Arg @Owning Waypoint waypoint) {

    }
}
