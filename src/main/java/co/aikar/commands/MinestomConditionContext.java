package co.aikar.commands;

import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Player;

public class MinestomConditionContext extends ConditionContext<MinestomCommandIssuer>{

    MinestomConditionContext(MinestomCommandIssuer issuer, String config) {
        super(issuer, config);
    }

    public CommandSender getSender() {
        return getIssuer().getIssuer();
    }

    public Player getPlayer() {
        return getIssuer().getPlayer();
    }
}
