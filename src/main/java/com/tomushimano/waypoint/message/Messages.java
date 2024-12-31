package com.tomushimano.waypoint.message;

import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.util.DontInvokeMe;

import static com.tomushimano.waypoint.message.MessageBuilder.keyed;
import static com.tomushimano.waypoint.message.Template.template;

public final class Messages {
    /* -~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~      A D M I N      ~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~- */
    public static final MessageBuilder.Preset0 ADMIN__RELOAD_INITIATED = keyed("admin.reload.initiated")
            .var0();

    public static final MessageBuilder.Preset0 ADMIN__RELOAD_FAILURE = keyed("admin.reload.failure")
            .var0();

    public static final MessageBuilder.Preset1<Long> ADMIN__RELOAD_SUCCESS = keyed("admin.reload.success")
            .var1((raw, duration) -> process(raw, template("duration", duration)));

    /* -~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~    C O M M A N D    ~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~- */
    public static final MessageBuilder.Preset0 COMMAND__ARG_OPTIONAL_CLOSE = keyed("command.arg_optional_close")
            .var0();

    public static final MessageBuilder.Preset0 COMMAND__ARG_OPTIONAL_OPEN = keyed("command.arg_optional_open")
            .var0();

    public static final MessageBuilder.Preset0 COMMAND__ARG_REQUIRED_CLOSE = keyed("command.arg_required_close")
            .var0();

    public static final MessageBuilder.Preset0 COMMAND__ARG_REQUIRED_OPEN = keyed("command.arg_required_open")
            .var0();

    public static final MessageBuilder.Preset0 COMMAND__CONFIRMATION_REQUIRED = keyed("command.confirmation_required")
            .var0();

    public static final MessageBuilder.Preset0 COMMAND__DUPLICATE_FLAG = keyed("command.duplicate_flag")
            .var0();

    public static final MessageBuilder.Preset1<String> COMMAND__INSUFFICIENT_PERMISSIONS = keyed("command.insufficient_permissions")
            .var1((raw, permission) -> process(raw, template("permission", permission)));



    /* -~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~ N A V I G A T I O N ~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~- */



    /* -~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~   W A Y P O I N T   ~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~- */
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

    public static final MessageBuilder.Preset1<Waypoint> WAYPOINT__DELETION_SUCCESS = keyed("waypoint.deletion.success")
            .var1((raw, waypoint) -> process(raw, template("name", waypoint.getName())));

    public static final MessageBuilder.Preset0 WAYPOINT__DELETION_FAILURE = keyed("waypoint.deletion.failure")
            .var0();

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
