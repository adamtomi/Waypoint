package com.tomushimano.waypoint.command.scaffold.condition;

import com.tomushimano.waypoint.config.Configurable;
import com.tomushimano.waypoint.di.qualifier.Lang;
import com.tomushimano.waypoint.message.Messages;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import grapefruit.command.argument.condition.CommandCondition;
import grapefruit.command.argument.condition.UnfulfilledConditionException;
import grapefruit.command.dispatcher.CommandContext;
import org.bukkit.command.CommandSender;

public class PermissionCondition implements CommandCondition<CommandSender> {
    private final String permission;
    private final Configurable config;

    @AssistedInject
    public PermissionCondition(final @Assisted String permission, final @Lang Configurable config) {
        this.permission = permission;
        this.config = config;
    }

    @Override
    public void test(final CommandContext<CommandSender> context) throws UnfulfilledConditionException {
        if (!context.source().hasPermission(this.permission)) {
            throw new VerboseConditionException(this, Messages.COMMAND__INSUFFICIENT_PERMISSIONS.from(this.config, permission).comp());
        }
    }

    @AssistedFactory
    public interface Factory {

        PermissionCondition create(final String permission);
    }
}
