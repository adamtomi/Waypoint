package com.tomushimano.waypoint.command.scaffold.mapper;

import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.core.WaypointService;
import grapefruit.command.CommandException;
import grapefruit.command.argument.CommandArgumentException;
import grapefruit.command.argument.mapper.ArgumentMapper;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.dispatcher.input.StringReader;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static com.tomushimano.waypoint.command.scaffold.WaypointContextKeys.PLAYER_KEY;

public class WaypointArgumentMapper implements ArgumentMapper<Waypoint> {
    public static final String OWNING_NAME = "__WAYPOINT_OWNING__";
    private final Function<Player, Set<Waypoint>> valueProvider;

    private WaypointArgumentMapper(Function<Player, Set<Waypoint>> valueProvider) {
        this.valueProvider = valueProvider;
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
                .orElseThrow(CommandArgumentException::new); // TODO error message?
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

        @Inject
        public Provider(final WaypointService waypointService) {
            this.waypointService = waypointService;
        }

        public WaypointArgumentMapper standard() {
            return new WaypointArgumentMapper(this.waypointService::getAccessibleWaypoints);
        }

        public WaypointArgumentMapper owning() {
            return new WaypointArgumentMapper(this.waypointService::getOwnedWaypoints);
        }
    }
}
