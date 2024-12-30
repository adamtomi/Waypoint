package com.tomushimano.waypoint.config.message;

import com.tomushimano.waypoint.config.ConfigKey;
import com.tomushimano.waypoint.util.DontInvokeMe;
import org.bukkit.configuration.ConfigurationSection;

public final class MessageKeys {
    
    public static ConfigKey<String> messageKey(final String key) {
        return new ConfigKey<>() {
            @Override
            public String key() {
                return key;
            }

            @Override
            public String readFrom(final ConfigurationSection config) {
                final String configured = config.getString(key);
                // Using the key itself as fallback. Not ideal, but at least
                // we don't get exceptions thrown around because of a missing
                // message key.
                if (configured == null) return key;
                
                return configured;
            }
        };
    }
    
    public static final class Admin {
        public static final ConfigKey<String> RELOAD_INITIATED = messageKey("admin.reload.initiated");
        public static final ConfigKey<String> RELOAD_SUCCESS = messageKey("admin.reload.success");
        public static final ConfigKey<String> RELOAD_FAILURE = messageKey("admin.reload.failure");

        private Admin() {
            throw new DontInvokeMe();
        }
    }

    public static final class Command {
        public static final ConfigKey<String> ARG_OPTIONAL_CLOSE = messageKey("command.arg_optional_close");
        public static final ConfigKey<String> ARG_OPTIONAL_OPEN = messageKey("command.arg_optional_open");
        public static final ConfigKey<String> ARG_REQUIRED_CLOSE = messageKey("command.arg_required_close");
        public static final ConfigKey<String> ARG_REQUIRED_OPEN = messageKey("command.arg_required_open");
        public static final ConfigKey<String> CONFIRMATION_REQUIRED = messageKey("command.confirmation_required");
        public static final ConfigKey<String> DUPLICATE_FLAG = messageKey("command.duplicate_flag");
        public static final ConfigKey<String> INSUFFICIENT_PERMISSIONS = messageKey("command.insufficient_permissions");
        public static final ConfigKey<String> INVALID_ARGUMENT = messageKey("command.invalid_argument");
        public static final ConfigKey<String> INVALID_FLAG = messageKey("command.invalid_flag");
        public static final ConfigKey<String> MALFORMED_COLOR = messageKey("command.malformed_color");
        public static final ConfigKey<String> MALFORMED_NUMBER = messageKey("command.malformed_number");
        public static final ConfigKey<String> MAX_LENGTH = messageKey("command.max_length");
        public static final ConfigKey<String> NEED_TO_BE_A_PLAYER = messageKey("command.need_to_be_a_player");
        public static final ConfigKey<String> NO_SUCH_COLOR = messageKey("command.no_such_color");
        public static final ConfigKey<String> RANGE_ERROR = messageKey("command.range_error");
        public static final ConfigKey<String> REGEX_ERROR = messageKey("command.regex_error");
        public static final ConfigKey<String> SYNTAX_HINT = messageKey("command.syntax_hint");
        public static final ConfigKey<String> SYNTAX_ERROR_TOO_FEW = messageKey("command.syntax_error_too_few");
        public static final ConfigKey<String> SYNTAX_ERROR_TOO_MANY = messageKey("command.syntax_error_too_many");
        public static final ConfigKey<String> UNEXPECTED_ERROR = messageKey("command.unexpected_error");
        public static final ConfigKey<String> UNKNOWN_SUBCOMMAND = messageKey("command.unknown_subcommand");
        public static final ConfigKey<String> UNKNOWN_SUBCOMMAND_ENTRY = messageKey("command.unknown_subcommand_entry");

        private Command() {
            throw new DontInvokeMe();
        }
    }

    public static final class Navigation {
        public static final ConfigKey<String> ARRIVED = messageKey("navigation.arrived");
        public static final ConfigKey<String> DISTANCE_INDICATOR = messageKey("navigation.distance_indicator");
        public static final ConfigKey<String> INFO = messageKey("navigation.info");
        public static final ConfigKey<String> INFO_NONE = messageKey("navigation.info_none");
        public static final ConfigKey<String> START_ALREADY_RUNNING = messageKey("navigation.start_already_running");
        public static final ConfigKey<String> START_RUNNING_CANCELLED = messageKey("navigation.start_running_cancelled");
        public static final ConfigKey<String> START_TOO_CLOSE = messageKey("navigation.start_too_close");
        public static final ConfigKey<String> START_WORLD_ERROR = messageKey("navigation.start_world_error");
        public static final ConfigKey<String> STARTED = messageKey("navigation.started");
        public static final ConfigKey<String> STOP_NONE_RUNNING = messageKey("navigation.stop_none_running");
        public static final ConfigKey<String> STOPPED = messageKey("navigation.stopped");

        private Navigation() {
            throw new DontInvokeMe();
        }
    }

    public static final class Waypoint {
        public static final ConfigKey<String> CREATION_SUCCESS = messageKey("waypoint.creation.success");
        public static final ConfigKey<String> CREATION_FAILURE = messageKey("waypoint.creation.failure");
        public static final ConfigKey<String> CREATION_ALREADY_EXISTS = messageKey("waypoint.creation.already_exists");

        public static final ConfigKey<String> DELETION_SUCCESS = messageKey("waypoint.deletion.success");
        public static final ConfigKey<String> DELETION_FAILURE = messageKey("waypoint.deletion.failure");

        public static final ConfigKey<String> DISTANCE = messageKey("waypoint.distance");

        public static final ConfigKey<String> INFO = messageKey("waypoint.info");

        public static final ConfigKey<String> LIST_HEADER = messageKey("waypoint.list.header");
        public static final ConfigKey<String> LIST_ITEM = messageKey("waypoint.list.item");
        public static final ConfigKey<String> LIST_ITEM_HOVER = messageKey("waypoint.list.item_hover");
        public static final ConfigKey<String> LIST_FOOTER_PREVIOUS = messageKey("waypoint.list.footer_previous");
        public static final ConfigKey<String> LIST_FOOTER_NEXT = messageKey("waypoint.list.footer_next");
        public static final ConfigKey<String> LIST_FOOTER_SEPARATOR = messageKey("waypoint.list.footer_separator");
        public static final ConfigKey<String> LIST_EMPTY = messageKey("waypoint.list.empty");

        public static final ConfigKey<String> NO_SUCH_WAYPOINT = messageKey("waypoint.no_such_waypoint");

        public static final ConfigKey<String> HOLOGRAM_COORDINATES = messageKey("waypoint.hologram.coordinates");
        public static final ConfigKey<String> HOLOGRAM_OWNER = messageKey("waypoint.hologram.owner");

        public static final ConfigKey<String> UPDATE_SUCCESS = messageKey("waypoint.update.success");
        public static final ConfigKey<String> UPDATE_FAILURE = messageKey("waypoint.update.failure");

        private Waypoint() {
            throw new DontInvokeMe();
        }
    }

    private MessageKeys() {
        throw new DontInvokeMe();
    }
}
