package com.tomushimano.waypoint.di.module;

import com.tomushimano.waypoint.command.impl.DistanceCommand;
import com.tomushimano.waypoint.command.impl.EditCommand;
import com.tomushimano.waypoint.command.impl.InfoCommand;
import com.tomushimano.waypoint.command.impl.ListCommand;
import com.tomushimano.waypoint.command.impl.NavigationInfoCommand;
import com.tomushimano.waypoint.command.impl.NavigationStartCommand;
import com.tomushimano.waypoint.command.impl.NavigationStopCommand;
import com.tomushimano.waypoint.command.impl.ReloadCommand;
import com.tomushimano.waypoint.command.impl.RelocateCommand;
import com.tomushimano.waypoint.command.impl.RemoveCommand;
import com.tomushimano.waypoint.command.impl.SetCommand;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;
import grapefruit.command.CommandModule;
import org.bukkit.command.CommandSender;

@Module
public interface CommandBinder {

    @Binds
    @IntoSet
    CommandModule<CommandSender> bindDistanceCommand(final DistanceCommand command);

    @Binds
    @IntoSet
    CommandModule<CommandSender> bindEditCommand(final EditCommand command);

    @Binds
    @IntoSet
    CommandModule<CommandSender> bindInfoCommand(final InfoCommand command);

    @Binds
    @IntoSet
    CommandModule<CommandSender> bindListCommand(final ListCommand command);

    @Binds
    @IntoSet
    CommandModule<CommandSender> bindNavigationInfoCommand(final NavigationInfoCommand command);

    @Binds
    @IntoSet
    CommandModule<CommandSender> bindNavigationStartCommand(final NavigationStartCommand command);

    @Binds
    @IntoSet
    CommandModule<CommandSender> bindNavigationStopCommand(final NavigationStopCommand command);

    @Binds
    @IntoSet
    CommandModule<CommandSender> bindReloadCommand(final ReloadCommand command);

    @Binds
    @IntoSet
    CommandModule<CommandSender> bindRelocateCommand(final RelocateCommand command);

    @Binds
    @IntoSet
    CommandModule<CommandSender> bindRemoveCommand(final RemoveCommand command);

    @Binds
    @IntoSet
    CommandModule<CommandSender> bindSetCommand(final SetCommand command);
}
