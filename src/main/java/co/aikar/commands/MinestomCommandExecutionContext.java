package co.aikar.commands;

import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Player;

import java.util.List;
import java.util.Map;

public class MinestomCommandExecutionContext extends CommandExecutionContext<MinestomCommandExecutionContext, MinestomCommandIssuer> {

    MinestomCommandExecutionContext(RegisteredCommand cmd, CommandParameter param, MinestomCommandIssuer sender, List<String> args,
                                    int index, Map<String, Object> passedArgs) {
        super(cmd, param, sender, args, index, passedArgs);
    }

    public CommandSender getSender() {
        return this.issuer.getIssuer();
    }

    public Player getPlayer() {
        return this.issuer.getPlayer();
    }

}
