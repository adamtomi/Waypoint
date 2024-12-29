package com.tomushimano.waypoint.command.scaffold.mapper;

import com.tomushimano.waypoint.command.scaffold.VerboseArgumentMappingException;
import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.core.WaypointService;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import grapefruit.command.argument.mapper.AbstractArgumentMapper;
import grapefruit.command.argument.mapper.ArgumentMappingException;
import grapefruit.command.completion.Completion;
import grapefruit.command.completion.CompletionSupport;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.dispatcher.input.CommandInputTokenizer;
import grapefruit.command.dispatcher.input.MissingInputException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public class WaypointArgumentMapper extends AbstractArgumentMapper<CommandSender, Waypoint> {
    private final BiFunction<WaypointService, Player, Set<Waypoint>> valueProvider;
    private final WaypointService waypointService;
    private final MessageConfig messageConfig;

    @AssistedInject
    public WaypointArgumentMapper(
            final @Assisted BiFunction<WaypointService, Player, Set<Waypoint>> valueProvider,
            final WaypointService waypointService,
            final MessageConfig messageConfig
    ) {
        super(Waypoint.class, false);
        this.valueProvider = valueProvider;
        this.waypointService = waypointService;
        this.messageConfig = messageConfig;
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

        return candidate.orElseThrow(() -> new VerboseArgumentMappingException(this.messageConfig.get(MessageKeys.Waypoint.NO_SUCH_WAYPOINT)
                .with(Placeholder.of("name", value))
                .make()));
    }

    @Override
    public List<Completion> complete(final CommandContext<CommandSender> context, final String input) {
        final Set<Waypoint> candidates = this.valueProvider.apply(this.waypointService, (Player) context.source());
        return CompletionSupport.strings(Waypoint::getName, candidates);
    }

    @AssistedFactory
    public interface Factory {

        WaypointArgumentMapper create(final BiFunction<WaypointService, Player, Set<Waypoint>> valueProvider);
    }
}
