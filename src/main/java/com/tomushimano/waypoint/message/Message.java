package com.tomushimano.waypoint.message;

import net.kyori.adventure.text.Component;

public interface Message extends Printable {

    String raw();

    Component comp();

    net.minecraft.network.chat.Component nms();
}
