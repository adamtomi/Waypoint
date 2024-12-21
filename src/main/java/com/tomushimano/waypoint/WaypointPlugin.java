package com.tomushimano.waypoint;

import org.bukkit.plugin.java.JavaPlugin;

import static java.util.Objects.requireNonNull;

public final class WaypointPlugin extends JavaPlugin {
    private final Runnable enableHook;
    private final Runnable disableHook;

    WaypointPlugin(
            final Runnable enableHook,
            final Runnable disableHook
    ) {
        this.enableHook = requireNonNull(enableHook, "enableHook cannot be null");
        this.disableHook = requireNonNull(disableHook, "disableHook cannot be null");
    }

    @Override
    public void onEnable() {
        this.enableHook.run();
    }

    @Override
    public void onDisable() {
        this.disableHook.run();
    }
}
