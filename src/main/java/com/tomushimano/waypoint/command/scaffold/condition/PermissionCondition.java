package com.tomushimano.waypoint.command.scaffold.condition;

import com.tomushimano.waypoint.message.Messages;
import grapefruit.command.argument.condition.CommandCondition;
import grapefruit.command.argument.condition.UnfulfilledConditionException;
import grapefruit.command.dispatcher.CommandContext;
import org.bukkit.command.CommandSender;

import static java.util.Objects.requireNonNull;

public class PermissionCondition implements CommandCondition.Early<CommandSender> {
    private final String permission;

    private PermissionCondition(final String permission) {
        this.permission = requireNonNull(permission, "permission cannot be null");
    }

    public static PermissionCondition perm(final String permission) {
        return new PermissionCondition(permission);
    }

    @Override
    public void testEarly(final CommandContext<CommandSender> context) throws UnfulfilledConditionException {
        if (!context.source().hasPermission(this.permission)) {
            throw new VerboseConditionException(this, config -> Messages.COMMAND__INSUFFICIENT_PERMISSIONS.from(config, permission).comp());
        }
    }
}
