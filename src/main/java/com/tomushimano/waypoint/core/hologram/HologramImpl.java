package com.tomushimano.waypoint.core.hologram;

import java.util.List;

import static java.util.Objects.requireNonNull;

final class HologramImpl implements Hologram {
    private final List<HologramLine> lines;

    HologramImpl(List<HologramLine> lines) {
        this.lines = requireNonNull(lines, "lines cannot be null");
    }

    @Override
    public List<HologramLine> lines() {
        return List.copyOf(this.lines);
    }
}
