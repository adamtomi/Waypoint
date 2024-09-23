package com.tomushimano.waypoint.config.message;

import com.tomushimano.waypoint.config.ConfigKey;
import com.tomushimano.waypoint.util.DontInvokeMe;

public final class MessageKeys {

    public static final class Command {
        public static final ConfigKey<String> NEED_TO_BE_A_PLAYER = ConfigKey.stringKey("command.need_to_be_a_player");
        public static final ConfigKey<String> UNEXPECTED_ERROR = ConfigKey.stringKey("command.unexpected_error");
        public static final ConfigKey<String> UNKNOWN_SUBCOMMAND = ConfigKey.stringKey("command.unknown_subcommand");

        private Command() {
            throw new DontInvokeMe();
        }
    }

    public static final class Waypoint {
        public static final ConfigKey<String> CREATION_SUCCESS = ConfigKey.stringKey("waypoint.creation.success");
        public static final ConfigKey<String> CREATION_FAILURE = ConfigKey.stringKey("waypoint.creation.failure");
        public static final ConfigKey<String> CREATION_ALREADY_EXISTS = ConfigKey.stringKey("waypoint.creation.already_exists");

        public static final ConfigKey<String> DELETION_SUCCESS = ConfigKey.stringKey("waypoint.deletion.success");
        public static final ConfigKey<String> DELETION_FAILURE = ConfigKey.stringKey("waypoint.deletion.failure");

        public static final ConfigKey<String> LIST_HEADER = ConfigKey.stringKey("waypoint.list.header");
        public static final ConfigKey<String> LIST_ITEM = ConfigKey.stringKey("waypoint.list.item");
        public static final ConfigKey<String> LIST_FOOTER_PREVIOUS = ConfigKey.stringKey("waypoint.list.footer_previous");
        public static final ConfigKey<String> LIST_FOOTER_NEXT = ConfigKey.stringKey("waypoint.list.footer_next");
        public static final ConfigKey<String> LIST_FOOTER_SEPARATOR = ConfigKey.stringKey("waypoint.list.footer_separator");
        public static final ConfigKey<String> LIST_EMPTY = ConfigKey.stringKey("waypoint.list.empty");

        private Waypoint() {
            throw new DontInvokeMe();
        }
    }

    private MessageKeys() {
        throw new DontInvokeMe();
    }
}
