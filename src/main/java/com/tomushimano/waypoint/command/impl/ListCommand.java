package com.tomushimano.waypoint.command.impl;

import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.core.WaypointService;
import com.tomushimano.waypoint.util.Paginator;
import grapefruit.command.CommandModule;
import grapefruit.command.argument.CommandArgument;
import grapefruit.command.argument.CommandChain;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.util.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

import java.util.Set;

import static com.tomushimano.waypoint.util.BukkitUtil.formatPosition;
import static grapefruit.command.argument.CommandArgument.literal;
import static net.kyori.adventure.text.event.ClickEvent.copyToClipboard;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.event.HoverEvent.showText;

public class ListCommand implements CommandModule<CommandSender> {
    private static final Key<Boolean> HIDE_GLOBAL_KEY = Key.named(Boolean.class, "hide-global");
    private static final Key<Integer> PAGE_KEY = Key.named(Integer.class, "page");
    private final WaypointService waypointService;
    private final MessageConfig messageConfig;

    @Inject
    public ListCommand(final WaypointService waypointService, final MessageConfig messageConfig) {
        this.waypointService = waypointService;
        this.messageConfig = messageConfig;
    }

    @Override
    public CommandChain<CommandSender> chain() {
        return CommandChain.<CommandSender>begin()
                .then(literal("waypoint").aliases("wp").build())
                .then(literal("list").aliases("ls").build())
                .flags()
                .then(CommandArgument.<CommandSender>presenceFlag(HIDE_GLOBAL_KEY).assumeShorthand().build())
                // TODO int mapper
                // .then(CommandArgument.<CommandSender, Integer>valueFlag(PAGE_KEY).assumeShorthand().build())
                .build();
    }

    @Override
    public void execute(final CommandContext<CommandSender> context) {
        final Player sender = (Player) context.source();
        final boolean hideGlobal = context.has(HIDE_GLOBAL_KEY);
        final int page = context.getOrDefault(PAGE_KEY, 0);

        final Set<Waypoint> waypoints = hideGlobal
                ? this.waypointService.getOwnedWaypoints(sender)
                : this.waypointService.getAccessibleWaypoints(sender);

        if (waypoints.isEmpty()) {
            sender.sendMessage(this.messageConfig.get(MessageKeys.Waypoint.LIST_EMPTY).make());
            return;
        }

        final Paginator<Waypoint> paginator = Paginator.create(waypoints.stream().sorted().toList());

        final Component prevButton = this.messageConfig.get(MessageKeys.Waypoint.LIST_FOOTER_PREVIOUS).make()
                .clickEvent(runCommand("/wp list -p %d".formatted(Math.max(0, page - 1))));
        final Component nextButton = this.messageConfig.get(MessageKeys.Waypoint.LIST_FOOTER_NEXT).make()
                .clickEvent(runCommand("/wp list -p %d".formatted(Math.min(paginator.total(), page + 1))));

        final Component footer = Component.text()
                .append(prevButton)
                .append(this.messageConfig.get(MessageKeys.Waypoint.LIST_FOOTER_SEPARATOR).make())
                .append(nextButton)
                .build();

        sender.sendMessage(this.messageConfig.get(MessageKeys.Waypoint.LIST_HEADER).with(
                        Placeholder.of("count", waypoints.size()),
                        Placeholder.of("page", paginator.normalize(page) + 1),
                        Placeholder.of("totalpages", paginator.total()))
                .make());

        for (final Waypoint waypoint : paginator.page(page)) {
            final String formattedPosition = formatPosition(waypoint.getPosition());
            final Component message = this.messageConfig.get(MessageKeys.Waypoint.LIST_ITEM).with(
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
}
