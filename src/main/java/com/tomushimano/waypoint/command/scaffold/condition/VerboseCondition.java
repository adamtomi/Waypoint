package com.tomushimano.waypoint.command.scaffold.condition;

import grapefruit.command.dispatcher.condition.CommandCondition;
import net.kyori.adventure.text.Component;

public interface VerboseCondition extends CommandCondition {

    Component describeFailure();
}
