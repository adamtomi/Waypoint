package com.tomushimano.waypoint.command.scaffold;

import com.tomushimano.waypoint.util.DontInvokeMe;
import grapefruit.command.runtime.util.key.Key;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Deprecated
public final class WaypointContextKeys {
    public static final String SENDER_KEY_NAME = "__SENDER__";
    public static final Key<CommandSender> SENDER_KEY = Key.named(CommandSender.class, SENDER_KEY_NAME);
    public static final Key<Player> PLAYER_KEY = Key.named(Player.class, SENDER_KEY_NAME);

    private WaypointContextKeys() {
        throw new DontInvokeMe();
    }
}
