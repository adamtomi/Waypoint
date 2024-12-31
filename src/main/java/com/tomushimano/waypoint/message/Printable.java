package com.tomushimano.waypoint.message;

import net.kyori.adventure.audience.Audience;

public interface Printable {

    void print(final Audience audience);

    void printActionBar(final Audience audience);
}
