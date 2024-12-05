package com.tomushimano.waypoint.command.scaffold.mapper;

import com.tomushimano.waypoint.command.scaffold.RichArgumentException;
import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.core.WaypointService;
import grapefruit.command.CommandException;
import grapefruit.command.argument.mapper.AbstractArgumentMapper;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.dispatcher.input.CommandInputTokenizer;
import io.leangen.geantyref.TypeToken;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class WaypointArgumentMapper extends AbstractArgumentMapper<CommandSender, Waypoint> {
    private static final TypeToken<Waypoint> TYPE = TypeToken.get(Waypoint.class);
    private final Function<Player, Set<Waypoint>> valueProvider;
    private final MessageConfig messageConfig;

    private WaypointArgumentMapper(Function<Player, Set<Waypoint>> valueProvider, MessageConfig messageConfig) {
        super(TYPE, false);
        this.valueProvider = valueProvider;
        this.messageConfig = messageConfig;
    }

    // TODO MappingResult.fromOptional()
    // TODO throws declaration
    @Override
    public Waypoint tryMap(final CommandContext context, final CommandInputTokenizer input) throws CommandException {
        final String value = input.readWord();
        if (!(context.source() instanceof Player player)) {
            throw new IllegalStateException("Command source was not a player. Perhaps a command chain has incorrect conditions?");
        }

        final Optional<Waypoint> candidate = this.valueProvider.apply(player).stream()
                .filter(x -> x.getName().equalsIgnoreCase(value))
                .findFirst();

        return candidate.orElseThrow(() -> RichArgumentException.fromInput(input, value, this.messageConfig.get(MessageKeys.Waypoint.NO_SUCH_WAYPOINT)
                .with(Placeholder.of("name", value))
                .make()));
    }

    /* @Override
    public List<String> complete(CommandContext context, String input) {
        // See this#dispatch for note
        Set<Waypoint> candidates = this.valueProvider.apply(context.require(PLAYER_KEY));
        return candidates.stream()
                .map(Waypoint::getName)
                .toList();
    } */

    public static final class Provider {
        private final WaypointService waypointService;
        private final MessageConfig messageConfig;

        @Inject
        public Provider(final WaypointService waypointService, final MessageConfig messageConfig) {
            this.waypointService = waypointService;
            this.messageConfig = messageConfig;
        }

        public WaypointArgumentMapper standard() {
            return new WaypointArgumentMapper(this.waypointService::getAccessibleWaypoints, this.messageConfig);
        }

        public WaypointArgumentMapper owning() {
            return new WaypointArgumentMapper(this.waypointService::getOwnedWaypoints, this.messageConfig);
        }
    }
}
