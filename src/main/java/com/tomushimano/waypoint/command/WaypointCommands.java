package com.tomushimano.waypoint.command;

import com.tomushimano.waypoint.command.scaffold.CommandModule;
import com.tomushimano.waypoint.command.scaffold.Owning;
import com.tomushimano.waypoint.command.scaffold.Sender;
import com.tomushimano.waypoint.command.scaffold.condition.IsPlayer;
import com.tomushimano.waypoint.command.scaffold.modifier.Max;
import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.core.WaypointService;
import com.tomushimano.waypoint.util.Paginator;
import com.tomushimano.waypoint.util.Position;
import grapefruit.command.runtime.annotation.Arg;
import grapefruit.command.runtime.annotation.Command;
import grapefruit.command.runtime.annotation.Flag;
import grapefruit.command.runtime.dispatcher.CommandDispatcher;
import grapefruit.command.runtime.generated.CommandContainer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.Set;

import static com.tomushimano.waypoint.util.BukkitUtil.formatPosition;
import static com.tomushimano.waypoint.util.ExceptionUtil.capture;
import static net.kyori.adventure.text.event.ClickEvent.copyToClipboard;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.event.HoverEvent.showText;

public class WaypointCommands implements CommandModule {
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

    @Command(route = "waypoint|wp set", permission = "waypoint.set", conditions = { IsPlayer.class })
    public void set(@Sender Player sender, @Arg @Max(255) String name, @Flag NamedTextColor color, @Flag boolean global) {
        // Check if a waypoint with this name exists already
        if (this.waypointService.getByName(sender, name).isPresent()) {
            sender.sendMessage(this.messageConfig.get(MessageKeys.Waypoint.CREATION_ALREADY_EXISTS)
                    .with(Placeholder.of("name", name))
                    .make());
            return;
        }

        this.waypointService.createWaypoint(sender, name, color, global)
                .thenApply(x -> this.messageConfig.get(MessageKeys.Waypoint.CREATION_SUCCESS)
                        .with(
                                Placeholder.of("name", x.getName()),
                                Placeholder.of("world", x.getPosition().getWorldName()),
                                Placeholder.of("coordinates", formatPosition(x.getPosition()))
                        )
                        .make())
                .thenAccept(sender::sendMessage)
                .exceptionally(capture(sender, this.messageConfig.get(MessageKeys.Waypoint.CREATION_FAILURE).make(), "Failed to create waypoint", LOGGER));
    }

    @Command(route = "waypoint|wp remove|rm", permission = "waypoint.remove", conditions = { IsPlayer.class })
    public void remove(@Sender Player sender, @Arg @Owning Waypoint waypoint) {
        this.waypointService.removeWaypoint(waypoint)
                .exceptionally(capture(sender, this.messageConfig.get(MessageKeys.Waypoint.DELETION_FAILURE).make(), "Failed to remove waypoint", LOGGER))
                .thenApply(x -> this.messageConfig.get(MessageKeys.Waypoint.DELETION_SUCCESS)
                        .with(Placeholder.of("name", waypoint.getName()))
                        .make())
                .thenAccept(sender::sendMessage);
    }

    @Command(route = "waypoint|wp list|ls", permission = "waypoint.list", conditions = { IsPlayer.class })
    public void list(@Sender Player sender, @Flag boolean hideGlobal, @Flag int page) {
        Set<Waypoint> waypoints = hideGlobal
                ? this.waypointService.getOwnedWaypoints(sender)
                : this.waypointService.getAccessibleWaypoints(sender);

        if (waypoints.isEmpty()) {
            sender.sendMessage(this.messageConfig.get(MessageKeys.Waypoint.LIST_EMPTY).make());
            return;
        }

        Paginator<Waypoint> paginator = Paginator.create(waypoints.stream().sorted().toList());

        Component prevButton = this.messageConfig.get(MessageKeys.Waypoint.LIST_FOOTER_PREVIOUS).make()
                        .clickEvent(runCommand("/wp list -p %d".formatted(Math.max(0, page - 1))));
        Component nextButton = this.messageConfig.get(MessageKeys.Waypoint.LIST_FOOTER_NEXT).make()
                        .clickEvent(runCommand("/wp list -p %d".formatted(Math.min(paginator.total(), page + 1))));

        Component footer = Component.text()
                .append(prevButton)
                .append(this.messageConfig.get(MessageKeys.Waypoint.LIST_FOOTER_SEPARATOR).make())
                .append(nextButton)
                .build();

        sender.sendMessage(this.messageConfig.get(MessageKeys.Waypoint.LIST_HEADER).with(
                        Placeholder.of("count", waypoints.size()),
                        Placeholder.of("page", paginator.normalize(page) + 1),
                        Placeholder.of("totalpages", paginator.total()))
                .make());

        for (Waypoint waypoint : paginator.page(page)) {
            String formattedPosition = formatPosition(waypoint.getPosition());
            Component message = this.messageConfig.get(MessageKeys.Waypoint.LIST_ITEM).with(
                            Placeholder.of("name", waypoint.getName()),
                            Placeholder.of("world", waypoint.getPosition().getWorldName()),
                            Placeholder.of("coordinates", formattedPosition))
                    .make()
                    .hoverEvent(showText(this.messageConfig.get(MessageKeys.Waypoint.LIST_ITEM_HOVER).make()))
                    .clickEvent(copyToClipboard(formattedPosition));
            sender.sendMessage(message);
        }

        sender.sendMessage(footer);
    }

    @Command(route = "waypoint|wp edit", permission = "waypoint.edit", conditions = { IsPlayer.class })
    public void edit(
            @Sender Player sender,
            @Arg @Owning Waypoint waypoint,
            @Flag @Max(255) String name,
            @Flag NamedTextColor color,
            @Flag boolean toggleGlobal
    ) {
        if (name != null) waypoint.setName(name);
        if (color != null) waypoint.setColor(color);
        if (toggleGlobal) waypoint.setGlobal(!waypoint.isGlobal());
        updateAndReport(sender, waypoint);
    }

    @Command(route = "waypoint|wp reloc|movehere", permission = "waypoint.reloc", conditions = { IsPlayer.class })
    public void reloc(@Sender Player sender, @Arg @Owning Waypoint waypoint) {
        waypoint.setPosition(Position.from(sender.getLocation()));
        updateAndReport(sender, waypoint);
    }

    private void updateAndReport(Player sender, Waypoint waypoint) {
        this.waypointService.updateWaypoint(waypoint)
                .thenApply(x -> this.messageConfig.get(MessageKeys.Waypoint.UPDATE_SUCCESS)
                        .with(Placeholder.of("name", waypoint.getName()))
                        .make())
                .thenAccept(sender::sendMessage)
                .exceptionally(capture(sender, this.messageConfig.get(MessageKeys.Waypoint.UPDATE_FAILURE).make(), "Failed to update waypoint", LOGGER));
    }
}
