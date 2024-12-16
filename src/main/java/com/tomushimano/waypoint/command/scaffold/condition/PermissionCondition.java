package com.tomushimano.waypoint.command.scaffold.condition;

import com.tomushimano.waypoint.config.message.MessageConfig;
import com.tomushimano.waypoint.config.message.MessageKeys;
import com.tomushimano.waypoint.config.message.Placeholder;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import grapefruit.command.argument.condition.CommandCondition;
import grapefruit.command.argument.condition.UnfulfilledConditionException;
import grapefruit.command.dispatcher.CommandContext;
import org.bukkit.command.CommandSender;

public class PermissionCondition implements CommandCondition<CommandSender> {
    private final String permission;
    private final MessageConfig messageConfig;

    @AssistedInject
    public PermissionCondition(final @Assisted String permission, final MessageConfig messageConfig) {
        this.permission = permission;
        this.messageConfig = messageConfig;
    }

    @Override
    public void test(final CommandContext<CommandSender> context) throws UnfulfilledConditionException {
        if (!context.source().hasPermission(this.permission)) {
            throw new VerboseConditionException(this, this.messageConfig.get(MessageKeys.Command.INSUFFICIENT_PERMISSIONS)
                    .with(Placeholder.of("permission", this.permission))
                    .make());
        }
    }

    @AssistedFactory
    public interface Factory {

        PermissionCondition create(final String permission);
    }
}
