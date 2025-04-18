package com.tomushimano.waypoint.message;

import com.tomushimano.waypoint.core.Waypoint;
import com.tomushimano.waypoint.util.BukkitUtil;
import com.tomushimano.waypoint.util.DontInvokeMe;
import com.tomushimano.waypoint.util.Position;
import org.bukkit.OfflinePlayer;

import java.util.Set;
import java.util.regex.Pattern;

import static com.tomushimano.waypoint.message.MessageBuilder.keyed;
import static com.tomushimano.waypoint.message.Template.template;
import static java.lang.String.join;

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

    public static final MessageBuilder.Preset3<String, String, String> COMMAND__INVALID_ARGUMENT = keyed("command.invalid_argument")
            .var3((raw, consumed, argument, remaining) -> process(
                    raw,
                    template("consumed", consumed),
                    template("argument", argument),
                    template("remaining", remaining)
            ));

    public static final MessageBuilder.Preset1<String> COMMAND__INVALID_FLAG = keyed("command.invalid_flag")
            .var1((raw, flag) -> process(raw, template("flag", flag)));

    public static final MessageBuilder.Preset0 COMMAND__MALFORMED_COLOR = keyed("command.malformed_color")
            .var0();


    public static final MessageBuilder.Preset0 COMMAND__MALFORMED_NUMBER = keyed("command.malformed_number")
            .var0();

    public static final MessageBuilder.Preset2<String, Integer> COMMAND__MAX_LENGTH = keyed("command.max_length")
            .var2((raw, argument, maxLength) -> process(
                    raw,
                    template("argument", argument),
                    template("max", maxLength)
            ));

    public static final MessageBuilder.Preset0 COMMAND__NEED_TO_BE_A_PLAYER = keyed("command.need_to_be_a_player")
            .var0();

    public static final MessageBuilder.Preset1<String> COMMAND__NO_SUCH_COLOR = keyed("command.no_such_color")
            .var1((raw, name) -> process(raw, template("name", name)));

    public static final MessageBuilder.Preset1<Integer> COMMAND__RANGE_ERROR = keyed("command.range_error")
            .var1((raw, min) -> process(raw, template("min", min)));

    public static final MessageBuilder.Preset1<Pattern> COMMAND__REGEX_ERROR = keyed("command.regex_error")
            .var1((raw, pattern) -> process(raw, template("regex", pattern.pattern())));

    public static final MessageBuilder.Preset1<String> COMMAND__SYNTAX_HINT = keyed("command.syntax_hint")
            .var1((raw, syntax) -> process(raw, template("syntax", syntax)));

    public static final MessageBuilder.Preset0 COMMAND__SYNTAX_ERROR_TOO_FEW = keyed("command.syntax_error_too_few")
            .var0();

    public static final MessageBuilder.Preset0 COMMAND__SYNTAX_ERROR_TOO_MANY = keyed("command.syntax_error_too_many")
            .var0();

    public static final MessageBuilder.Preset0 COMMAND__UNEXPECTED_ERROR = keyed("command.unexpected_error")
            .var0();

    public static final MessageBuilder.Preset0 COMMAND__UNKNOWN_SUBCOMMAND = keyed("command.unknown_subcommand")
            .var0();

    public static final MessageBuilder.Preset3<String, String, Set<String>> COMMNAD__UNKNOWN_SUBCOMMAND_ENTRY = keyed("command.unknown_subcommand_entry")
            .var3((raw, prefix, name, aliases) -> process(
                    raw,
                    template("prefix", prefix),
                    template("name", name),
                    template("aliases", join(", ", aliases))
            ));


    /* -~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~ N A V I G A T I O N ~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~- */
    public static final MessageBuilder.Preset1<Waypoint> NAVIGATION__ARRIVED = keyed("navigation.arrived")
            .var1((raw, waypoint) -> process(raw, template("name", waypoint.getName())));

    public static final MessageBuilder.Preset1<Long> NAVIGATION__DISTANCE_INDICATOR = keyed("navigation.distance_indicator")
            .var1((raw, distance) -> process(raw, template("blocks", distance)));

    public static final MessageBuilder.Preset1<Waypoint> NAVIGATION__INFO = keyed("navigation.info")
            .var1((raw, waypoint) -> process(raw, template("name", waypoint.getName())));

    public static final MessageBuilder.Preset0 NAVIGATION__INFO_NONE = keyed("navigation.info_none").var0();

    public static final MessageBuilder.Preset0 NAVIGATION__START_ALREAEDY_RUNNING = keyed("navigation.start_already_running")
            .var0();

    public static final MessageBuilder.Preset1<Waypoint> NAVIGATION__START_RUNNING_CANCELLED = keyed("navigation.start_running_cancelled")
            .var1((raw, waypoint) -> process(raw, template("name", waypoint.getName())));

    public static final MessageBuilder.Preset1<Integer> NAVIGATION__START_TOO_CLOSE = keyed("navigation.start_too_close")
            .var1((raw, min) -> process(raw, template("blocks", min)));

    public static final MessageBuilder.Preset1<Waypoint> NAVIGATION__STARTED = keyed("navigation.started")
            .var1((raw, waypoint) -> process(raw, template("name", waypoint.getName())));

    public static final MessageBuilder.Preset0 NAVIGATION__STOP_NONE_RUNNING = keyed("navigation.stop_none_running")
            .var0();

    public static final MessageBuilder.Preset1<Waypoint> NAVIGATION__STOPPED = keyed("navigation.stopped")
            .var1((raw, waypoint) -> process(raw, template("name", waypoint.getName())));


    /* -~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~   W A Y P O I N T   ~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~- */
    public static final MessageBuilder.Preset1<String> WAYPOINT__CREATION_ALREADY_EXISTS = keyed("waypoint.creation.already_exists")
            .var1((raw, name) -> process(raw, template("name", name)));

    public static final MessageBuilder.Preset0 WAYPOINT__CREATION_FAILURE = keyed("waypoint.creation.failure")
            .var0();

    public static final MessageBuilder.Preset1<Waypoint> WAYPOINT__CREATION_SUCCESS = keyed("waypoint.creation.success")
            .var1((raw, waypoint) -> process(
                    raw,
                    concat(
                            posToTemplates(waypoint.getPosition()),
                            template("name", waypoint.getName())
                    )
            ));

    public static final MessageBuilder.Preset1<Waypoint> WAYPOINT__DELETION_SUCCESS = keyed("waypoint.deletion.success")
            .var1((raw, waypoint) -> process(raw, template("name", waypoint.getName())));

    public static final MessageBuilder.Preset0 WAYPOINT__DELETION_FAILURE = keyed("waypoint.deletion.failure")
            .var0();

    public static final MessageBuilder.Preset1<Long> WAYPOINT__DISTANCE = keyed("waypoint.distance")
            .var1((raw, distance) -> process(raw, template("distance", distance)));

    public static final MessageBuilder.Preset2<Waypoint, OfflinePlayer> WAYPOINT__INFO = keyed("waypoint.info")
            .var2((raw, waypoint, owner) -> process(
                    raw,
                    concat(
                            posToTemplates(waypoint.getPosition()),
                            template("name", waypoint.getName()),
                            template("uniqueId", waypoint.getUniqueId()),
                            template("owner", owner.getName()),
                            template("public", waypoint.isPublic()),
                            template("color", waypoint.getColor().asHexString()),
                            template("colorname", BukkitUtil.getColorName(waypoint.getColor()))
                    )
            ));

    public static final MessageBuilder.Preset3<Integer, Integer, Integer> WAYPOINT__LIST_HEADER = keyed("waypoint.list.header")
            .var3((raw, count, page, total) -> process(
                    raw,
                    template("count", count),
                    template("page", page),
                    template("total", total)
            ));

    public static final MessageBuilder.Preset1<Waypoint> WAYPOINT__LIST_ITEM = keyed("waypoint.list.item")
            .var1((raw, waypoint) -> process(
                    raw,
                    concat(posToTemplates(waypoint.getPosition()), template("name", waypoint.getName()))
            ));

    public static final MessageBuilder.Preset0 WAYPOINT__LIST_ITEM_HOVER = keyed("waypoint.list.item_hover")
            .var0();

    public static final MessageBuilder.Preset0 WAYPOINT__LIST_FOOTER_PREVIOUS = keyed("waypoint.list.footer_previous")
            .var0();

    public static final MessageBuilder.Preset0 WAYPOINT__LIST_FOOTER_SEPARATOR = keyed("waypoint.list.footer_separator")
            .var0();

    public static final MessageBuilder.Preset0 WAYPOINT__LIST_FOOTER_NEXT = keyed("waypoint.list.footer_next")
            .var0();

    public static final MessageBuilder.Preset0 WAYPOINT__LIST_EMPTY = keyed("waypoint.list.empty")
            .var0();

    public static final MessageBuilder.Preset1<Waypoint> WAYPOINT__HOLOGRAM_COORDINATES = keyed("waypoint.hologram.coordinates")
            .var1((raw, waypoint) -> process(raw, posToTemplates(waypoint.getPosition())));

    public static final MessageBuilder.Preset1<OfflinePlayer> WAYPOINT__HOLOGRAM_OWNER = keyed("waypoint.hologram.owner")
            .var1((raw, owner) -> process(raw, template("owner", owner.getName())));

    public static final MessageBuilder.Preset1<String> WAYPOINT__NO_SUCH_WAYPOINT = keyed("waypoint.no_such_waypoint")
            .var1((raw, name) -> process(raw, template("name", name)));

    public static final MessageBuilder.Preset1<Waypoint> WAYPOINT__UPDATE_SUCCESS = keyed("waypoint.update.success")
            .var1((raw, waypoint) -> process(raw, template("name", waypoint.getName())));

    public static final MessageBuilder.Preset0 WAYPOINT__UPDATE_FAILURE = keyed("waypoint.update.failure")
            .var0();

    public static final MessageBuilder.Preset1<Waypoint> WAYPOINT__WORLD_ERROR = keyed("waypoint.world_error")
            .var1((raw, waypoint) -> process(raw, template("name", waypoint.getPosition().getWorldName())));

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

    private static Template[] posToTemplates(final Position position) {
        final Template[] templates = new Template[4];
        templates[0] = template("world", position.getWorldName());
        templates[1] = template("x", position.getX());
        templates[2] = template("y", position.getY());
        templates[3] = template("z", position.getZ());
        return templates;
    }

    private static Template[] concat(final Template[] first, final Template... second) {
        final Template[] result = new Template[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
