package com.tomushimano.waypoint.command.scaffold.mapper;

import com.tomushimano.waypoint.command.scaffold.VerboseArgumentMappingException;
import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.core.WaypointService;
import com.tomushimano.waypoint.di.qualifier.Lang;
import com.tomushimano.waypoint.message.Messages;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import grapefruit.command.argument.mapper.AbstractArgumentMapper;
import grapefruit.command.argument.mapper.ArgumentMappingException;
import grapefruit.command.completion.CompletionAccumulator;
import grapefruit.command.completion.CompletionBuilder;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.dispatcher.input.CommandInputTokenizer;
import grapefruit.command.dispatcher.input.MissingInputException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public class WaypointArgumentMapper extends AbstractArgumentMapper<CommandSender, Waypoint> {
    private final BiFunction<WaypointService, Player, Set<Waypoint>> valueProvider;
    private final WaypointService waypointService;
    private final Configurable config;

    @AssistedInject
    public WaypointArgumentMapper(
            final @Assisted BiFunction<WaypointService, Player, Set<Waypoint>> valueProvider,
            final WaypointService waypointService,
            final @Lang Configurable config
    ) {
        super(Waypoint.class, false);
        this.valueProvider = valueProvider;
        this.waypointService = waypointService;
        this.config = config;
    }

    @Override
    public Waypoint tryMap(final CommandContext<CommandSender> context, final CommandInputTokenizer input) throws ArgumentMappingException, MissingInputException {
        final String value = input.readWord();
        if (!(context.source() instanceof Player player)) {
            throw new IllegalStateException("Command source was not a player. Perhaps a command chain has incorrect conditions?");
        }

        final Optional<Waypoint> candidate = this.valueProvider.apply(this.waypointService,  player).stream()
                .filter(x -> x.getName().equalsIgnoreCase(value))
                .findFirst();

        return candidate.orElseThrow(() -> new VerboseArgumentMappingException(Messages.WAYPOINT__NO_SUCH_WAYPOINT.from(this.config, value).comp()));
    }

    @Override
    public CompletionAccumulator complete(final CommandContext<CommandSender> context, final CompletionBuilder builder) {
        final Set<Waypoint> candidates = this.valueProvider.apply(this.waypointService, (Player) context.source());
        return builder.includeStrings(candidates, Waypoint::getName).build();
    }

    @AssistedFactory
    public interface Factory {

        WaypointArgumentMapper create(final BiFunction<WaypointService, Player, Set<Waypoint>> valueProvider);
    }
}
