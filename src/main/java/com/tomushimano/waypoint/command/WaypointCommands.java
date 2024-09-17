package com.tomushimano.waypoint.command;

import com.tomushimano.waypoint.command.scaffold.CommandHolder;
import com.tomushimano.waypoint.command.scaffold.Owning;
import com.tomushimano.waypoint.command.scaffold.Sender;
import com.tomushimano.waypoint.command.scaffold.condition.IsPlayer;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.core.WaypointService;
import grapefruit.command.CommandContainer;
import grapefruit.command.annotation.CommandDefinition;
import grapefruit.command.annotation.argument.Arg;
import grapefruit.command.annotation.argument.Flag;
import grapefruit.command.dispatcher.CommandDispatcher;
import org.bukkit.entity.Player;

import javax.inject.Inject;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;

public class WaypointCommands implements CommandHolder {
    private final CommandContainer container = new WaypointCommands_Container(this);
    private final WaypointService waypointService;

    @Inject
    public WaypointCommands(final WaypointService waypointService) {
        this.waypointService = waypointService;
    }

    @Override
    public void registerCommands(CommandDispatcher dispatcher) {
        dispatcher.register(this.container);
    }

    @CommandDefinition(route = "waypoint|wp set", permission = "waypoint.set", conditions = { IsPlayer.class })
    public void set(@Sender Player sender, @Arg String name, @Flag String displayName, @Flag boolean global) {
        this.waypointService.createWaypoint(name, displayName, global)
                .thenRun(() -> sender.sendMessage(text("Your waypoint has been created", GREEN)));
    }

    @CommandDefinition(route = "waypoint|wp remove|rm", permission = "waypoint.remove", conditions = { IsPlayer.class })
    public void remove(@Sender Player sender, @Arg @Owning Waypoint waypoint) {

    }

    @CommandDefinition(route = "waypoint|wp list|ls", permission = "waypoint.list", conditions = { IsPlayer.class })
    public void list(@Sender Player sender, @Flag boolean hideGlobals) {

    }

    @CommandDefinition(route = "waypoint|wp toggle", permission = "waypoint.toggle", conditions = { IsPlayer.class })
    public void toggle(@Sender Player sender, @Flag Waypoint waypoint) {

    }

    @CommandDefinition(route = "waypoint|wp edit", permission = "waypoint.edit", conditions = { IsPlayer.class })
    public void edit(@Sender Player sender, @Owning @Arg Waypoint waypoint, @Flag String name, @Flag String displayName, @Flag boolean toggleGlobal) {

    }

    @CommandDefinition(route = "waypoint|wp reloc|movehere", permission = "waypoint.reloc", conditions = { IsPlayer.class })
    public void reloc(@Sender Player sender, @Arg @Owning Waypoint waypoint) {

    }
}
