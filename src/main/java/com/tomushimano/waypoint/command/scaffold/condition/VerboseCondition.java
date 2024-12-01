package com.tomushimano.waypoint.command.scaffold.condition;

import grapefruit.command.runtime.dispatcher.condition.CommandCondition;
import net.kyori.adventure.text.Component;

@Deprecated
public interface VerboseCondition extends CommandCondition {

    Component describeFailure();
}
