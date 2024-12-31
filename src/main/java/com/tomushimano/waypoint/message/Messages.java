package com.tomushimano.waypoint.message;

import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.util.DontInvokeMe;

import static com.tomushimano.waypoint.message.MessageBuilder.keyed;
import static com.tomushimano.waypoint.message.Template.template;

public final class Messages {
    public static final MessageBuilder.Preset1<String> WAYPOINT__CREATION_ALREADY_EXISTS = keyed("waypoint.creation.already_exists")
            .var1((raw, name) -> process(raw, template("name", name)));
    public static final MessageBuilder.Preset0 WAYPOINT__CREATION_FAILURE = keyed("waypoint.creation.failure")
            .var0();
    public static final MessageBuilder.Preset1<Waypoint> WAYPOINT__CREATION_SUCCESS = keyed("waypoint.creation.success")
            .var1((raw, waypoint) -> process(
                    raw,
                    template("name", waypoint.getName()),
                    template("x", waypoint.getPosition().getX()),
                    template("y", waypoint.getPosition().getY()),
                    template("z", waypoint.getPosition().getZ()),
                    template("world", waypoint.getPosition().getWorldName())
            ));

    private Messages() {
        throw new DontInvokeMe();
    }

    private static String process(final String raw, final Template... templates) {
        String output = raw;
        for (final Template template : templates) {
            output = template.process(output);
        }

        return output;
    }
}
