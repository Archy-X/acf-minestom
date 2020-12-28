package co.aikar.commands;

import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Player;

public class MinestomCommandCompletionContext extends CommandCompletionContext<MinestomCommandIssuer> {

    MinestomCommandCompletionContext(RegisteredCommand command, MinestomCommandIssuer issuer, String input, String config, String[] args) {
        super(command, issuer, input, config, args);
    }

    public CommandSender getSender() {
        return this.getIssuer().getIssuer();
    }

    public Player getPlayer() {
        return this.issuer.getPlayer();
    }

}
