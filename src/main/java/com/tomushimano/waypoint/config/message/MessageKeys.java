package com.tomushimano.waypoint.config.message;

import com.tomushimano.waypoint.config.ConfigKey;
import com.tomushimano.waypoint.util.DontInvokeMe;

public final class MessageKeys {

    public static final class Command {
        public static final ConfigKey<String> NEED_TO_BE_A_PLAYER = ConfigKey.stringKey("command.need-to-be-a-player");
        public static final ConfigKey<String> UNEXPECTED_ERROR = ConfigKey.stringKey("command.unexpected-error");
        public static final ConfigKey<String> UNKNOWN_SUBCOMMAND = ConfigKey.stringKey("command.unknown-subcommand");

        private Command() {
            throw new DontInvokeMe();
        }
    }

    public static final class Waypoint {
        public static final ConfigKey<String> CREATION_SUCCESS = ConfigKey.stringKey("waypoint.creation.success");
        public static final ConfigKey<String> CREATION_FAILURE = ConfigKey.stringKey("waypoint.creation.failure");
        public static final ConfigKey<String> DELETION_SUCCESS = ConfigKey.stringKey("waypoint.deletion.success");
        public static final ConfigKey<String> DELETION_FAILURE = ConfigKey.stringKey("waypoint.deletion.failure");

        private Waypoint() {
            throw new DontInvokeMe();
        }
    }

    private MessageKeys() {
        throw new DontInvokeMe();
    }
}
