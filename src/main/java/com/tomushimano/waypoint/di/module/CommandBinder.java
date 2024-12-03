package com.tomushimano.waypoint.di.module;

import com.tomushimano.waypoint.command.impl.EditCommand;
import com.tomushimano.waypoint.command.impl.InfoCommand;
import com.tomushimano.waypoint.command.impl.ListCommand;
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
    CommandModule<CommandSender> bindEditCommand(EditCommand command);

    @Binds
    @IntoSet
    CommandModule<CommandSender> bindInfoCommand(InfoCommand command);

    @Binds
    @IntoSet
    CommandModule<CommandSender> bindListCommand(ListCommand command);

    @Binds
    @IntoSet
    CommandModule<CommandSender> bindReloadCommand(ReloadCommand command);

    @Binds
    @IntoSet
    CommandModule<CommandSender> bindRelocateCommand(RelocateCommand command);

    @Binds
    @IntoSet
    CommandModule<CommandSender> bindRemoveCommand(RemoveCommand command);

    @Binds
    @IntoSet
    CommandModule<CommandSender> bindSetCommand(SetCommand command);
}
