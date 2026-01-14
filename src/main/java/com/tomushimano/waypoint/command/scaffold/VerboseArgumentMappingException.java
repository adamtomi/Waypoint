package com.tomushimano.waypoint.command.scaffold;

import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.util.VerboseException;
import grapefruit.command.argument.mapper.ArgumentMappingException;
import net.kyori.adventure.text.Component;

import java.io.Serial;

import static java.util.Objects.requireNonNull;

public class VerboseArgumentMappingException extends ArgumentMappingException implements VerboseException {
    @Serial
    private static final long serialVersionUID = 2861564508491967866L;
    private final ComponentProvider provider;
    // private final Component richMessage;

    /*
    public VerboseArgumentMappingException(final Component richMessage) {
        this.richMessage = requireNonNull(richMessage, "richMessage cannot be null");
    }
    */

    public VerboseArgumentMappingException(final ComponentProvider provider) {
        this.provider = requireNonNull(provider, "provider cannot be null");
    }

    @Override
    public Component describeFailure(final Configurable config) {
        // return this.richMessage;
        return this.provider.provide(config);
    }
}
