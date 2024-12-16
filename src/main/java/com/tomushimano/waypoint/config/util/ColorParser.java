package com.tomushimano.waypoint.config.util;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.function.BiFunction;

public class ColorParser implements BiFunction<ConfigurationSection, String, Color> {
    public static final ColorParser INSTANCE = new ColorParser();
    private static final char HASH = '#';
    private static final Map<String, Color> COLOR_BY_NAME = ImmutableMap.<String, Color>builder()
            .put("white", Color.WHITE)
            .put("silver", Color.SILVER)
            .put("gray", Color.GRAY)
            .put("black", Color.BLACK)
            .put("red", Color.RED)
            .put("maroon", Color.MAROON)
            .put("yellow", Color.YELLOW)
            .put("olive", Color.OLIVE)
            .put("lime", Color.LIME)
            .put("green", Color.GREEN)
            .put("aqua", Color.AQUA)
            .put("teal", Color.TEAL)
            .put("blue", Color.BLUE)
            .put("navy", Color.NAVY)
            .put("fuchsia", Color.FUCHSIA)
            .put("purple", Color.PURPLE)
            .put("orange", Color.ORANGE)
            .build();

    private ColorParser() {}

    @Override
    public Color apply(final ConfigurationSection config, final String key) {
        final String value = config.getString(key);
        if (value == null) return null;

        if (value.isBlank()) {
            throw new IllegalArgumentException("Empty color value configured at: %s".formatted(key));
        }

        final Color color = COLOR_BY_NAME.get(value.toLowerCase());
        if (color != null) return color;

        try {
            final int colorValue = Integer.parseInt(value.charAt(0) == HASH ? value.substring(1) : value, 16);
            return Color.fromRGB(colorValue);
        } catch (final NumberFormatException ex) {
            throw new IllegalArgumentException("Illegal color value (\"%s\") configured at: %s".formatted(value, key));
        }
    }
}
