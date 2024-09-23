package com.tomushimano.waypoint.command;

import com.tomushimano.waypoint.command.scaffold.CommandHolder;
import com.tomushimano.waypoint.command.scaffold.Owning;
import com.tomushimano.waypoint.command.scaffold.Sender;
import com.tomushimano.waypoint.command.scaffold.condition.IsPlayer;
import com.tomushimano.waypoint.command.scaffold.modifier.Max;
import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.core.WaypointService;
import com.tomushimano.waypoint.util.NamespacedLoggerFactory;
import com.tomushimano.waypoint.util.Paginated;
import com.tomushimano.waypoint.util.Position;
import grapefruit.command.CommandContainer;
import grapefruit.command.annotation.CommandDefinition;
import grapefruit.command.annotation.argument.Arg;
import grapefruit.command.annotation.argument.Flag;
import grapefruit.command.dispatcher.CommandDispatcher;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;
import org.slf4j.Logger;

import javax.inject.Inject;

import java.util.Set;

import static com.tomushimano.waypoint.util.BukkitUtil.formatPosition;
import static com.tomushimano.waypoint.util.ExceptionUtil.capture;

public class WaypointCommands implements CommandHolder {
    private static final Logger LOGGER = NamespacedLoggerFactory.create("Commands");
    private final CommandContainer container = new WaypointCommands_Container(this);
    private final WaypointService waypointService;
    private final MessageConfig messageConfig;

    @Inject
    public WaypointCommands(WaypointService waypointService, MessageConfig messageConfig) {
        this.waypointService = waypointService;
        this.messageConfig = messageConfig;
    }

    @Override
    public void registerCommands(CommandDispatcher dispatcher) {
        dispatcher.register(this.container);
    }

    @CommandDefinition(route = "waypoint|wp set", permission = "waypoint.set", conditions = { IsPlayer.class })
    public void set(@Sender Player sender, @Arg @Max(255) String name, @Flag boolean global) {
        // Check if a waypoint with this name exists already
        if (this.waypointService.getByName(sender, name).isPresent()) {
            sender.sendMessage(this.messageConfig.get(MessageKeys.Waypoint.CREATION_ALREADY_EXISTS)
                    .with(Placeholder.of("name", name))
                    .make());
            return;
        }

        Waypoint waypoint = this.waypointService.createWaypoint(sender, name, global)
                //
                .join();

        Position pos = waypoint.getPosition();

        sender.sendMessage(this.messageConfig.get(MessageKeys.Waypoint.CREATION_SUCCESS)
                .with(
                        Placeholder.of("name", waypoint.getName()),
                        Placeholder.of("world", pos.getWorldName()),
                        Placeholder.of("coordinates", formatPosition(pos))
                )
                .make());
    }

    @CommandDefinition(route = "waypoint|wp remove|rm", permission = "waypoint.remove", conditions = { IsPlayer.class })
    public void remove(@Sender Player sender, @Arg @Owning Waypoint waypoint) {
        this.waypointService.removeWaypoint(waypoint)
                .exceptionally(capture(sender, this.messageConfig.get(MessageKeys.Waypoint.DELETION_FAILURE).make(), "Failed to remove waypoint", LOGGER))
                .thenApply(x -> this.messageConfig.get(MessageKeys.Waypoint.DELETION_SUCCESS)
                        .with(Placeholder.of("name", waypoint.getName()))
                        .make())
                .thenAccept(sender::sendMessage);
    }

    @CommandDefinition(route = "waypoint|wp list|ls", permission = "waypoint.list", conditions = { IsPlayer.class })
    public void list(@Sender Player sender, @Flag boolean hideGlobal, @Flag int page) {
        Set<Waypoint> waypoints = hideGlobal
                ? this.waypointService.getOwnedWaypoints(sender)
                : this.waypointService.getAccessibleWaypoints(sender);

        if (waypoints.isEmpty()) {
            sender.sendMessage(this.messageConfig.get(MessageKeys.Waypoint.LIST_EMPTY).make());
            return;
        }

        Paginated<Waypoint> paginated = Paginated.<Waypoint>builder()
                .items(waypoints)
                .formatItem(x -> this.messageConfig.get(MessageKeys.Waypoint.LIST_ITEM)
                        .with(
                                Placeholder.of("name", x.getName()),
                                Placeholder.of("world", x.getPosition().getWorldName()),
                                Placeholder.of("coordinates", formatPosition(x.getPosition()))
                        )
                        .make())
                .page(page)
                .build();

        Component footer = Component.text()
                .append(this.messageConfig.get(MessageKeys.Waypoint.LIST_FOOTER_PREVIOUS).make())
                .clickEvent(ClickEvent.runCommand("wp list %d".formatted(Math.max(0, page - 1))))
                .append(this.messageConfig.get(MessageKeys.Waypoint.LIST_FOOTER_SEPARATOR).make())
                .append(this.messageConfig.get(MessageKeys.Waypoint.LIST_FOOTER_NEXT).make())
                .clickEvent(ClickEvent.runCommand("wp list %d".formatted(Math.min(paginated.totalPages(), page + 1))))
                .build();

        sender.sendMessage(this.messageConfig.get(MessageKeys.Waypoint.LIST_HEADER)
                .with(
                        Placeholder.of("count", waypoints.size()),
                        Placeholder.of("page", paginated.currentPage() + 1),
                        Placeholder.of("totalpages", paginated.totalPages())
                )
                .make());
        paginated.viewPage().forEach(sender::sendMessage);
        sender.sendMessage(footer);
    }

    @CommandDefinition(route = "waypoint|wp toggle", permission = "waypoint.toggle", conditions = { IsPlayer.class })
    public void toggle(@Sender Player sender, @Flag Waypoint waypoint) {

    }

    @CommandDefinition(route = "waypoint|wp edit", permission = "waypoint.edit", conditions = { IsPlayer.class })
    public void edit(@Sender Player sender, @Arg @Owning  Waypoint waypoint, @Flag @Max(255) String name, @Flag boolean toggleGlobal) {

    }

    @CommandDefinition(route = "waypoint|wp reloc|movehere", permission = "waypoint.reloc", conditions = { IsPlayer.class })
    public void reloc(@Sender Player sender, @Arg @Owning Waypoint waypoint) {

    }
}
