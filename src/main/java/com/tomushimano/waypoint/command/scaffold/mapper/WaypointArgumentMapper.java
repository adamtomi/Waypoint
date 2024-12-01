package com.tomushimano.waypoint.command.scaffold.mapper;

import com.tomushimano.waypoint.command.scaffold.RichCommandException;
import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.core.WaypointService;
import grapefruit.command.runtime.CommandException;
import grapefruit.command.runtime.argument.mapper.ArgumentMapper;
import grapefruit.command.runtime.dispatcher.CommandContext;
import grapefruit.command.runtime.dispatcher.input.StringReader;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static com.tomushimano.waypoint.command.scaffold.WaypointContextKeys.PLAYER_KEY;

@Deprecated
public class WaypointArgumentMapper implements ArgumentMapper<Waypoint> {
    public static final String OWNING_NAME = "__WAYPOINT_OWNING__";
    private final Function<Player, Set<Waypoint>> valueProvider;
    private final MessageConfig messageConfig;

    private WaypointArgumentMapper(Function<Player, Set<Waypoint>> valueProvider, MessageConfig messageConfig) {
        this.valueProvider = valueProvider;
        this.messageConfig = messageConfig;
    }

    @Override
    public Waypoint tryMap(CommandContext context, StringReader input) throws CommandException {
        String value = input.readSingle();
        /*
         * This will not fail, because by the time we get to this point,
         * conditions have already been checked, and the IsPlayer condition
         * would've caught this.
         */
        Set<Waypoint> candidates = this.valueProvider.apply(context.require(PLAYER_KEY));
        return candidates.stream()
                .filter(x -> x.getName().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new RichCommandException(this.messageConfig.get(MessageKeys.Waypoint.NO_SUCH_WAYPOINT)
                        .with(Placeholder.of("name", value))
                        .make()));
    }

    @Override
    public List<String> complete(CommandContext context, String input) {
        // See this#dispatch for note
        Set<Waypoint> candidates = this.valueProvider.apply(context.require(PLAYER_KEY));
        return candidates.stream()
                .map(Waypoint::getName)
                .toList();
    }

    public static final class Provider {
        private final WaypointService waypointService;
        private final MessageConfig messageConfig;

        @Inject
        public Provider(WaypointService waypointService, MessageConfig messageConfig) {
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
