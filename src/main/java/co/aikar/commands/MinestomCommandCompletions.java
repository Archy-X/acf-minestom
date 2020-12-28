package co.aikar.commands;

import net.minestom.server.MinecraftServer;
import net.minestom.server.chat.ChatColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MinestomCommandCompletions extends CommandCompletions<MinestomCommandCompletionContext> {

    public MinestomCommandCompletions(MinestomCommandManager manager) {
        super(manager);
        registerAsyncCompletion("mobs", c -> {
            final Stream<String> normal = Stream.of(EntityType.values())
                    .map(entityType -> ACFUtil.simplifyString(entityType.name()));
            return normal.collect(Collectors.toList());
        });
        registerAsyncCompletion("chatcolors", c -> {
            Stream<ChatColor> colors = Stream.of(ACFMinestomUtil.getAllChatColors());
            if (c.hasConfig("colorsonly")) {
                colors = colors.filter(color -> !color.isSpecial());
            }
            String filter = c.getConfig("filter");
            if (filter != null) {
                Set<String> filters = Arrays.stream(ACFPatterns.COLON.split(filter))
                        .map(ACFUtil::simplifyString).collect(Collectors.toSet());

                colors = colors.filter(color -> filters.contains(ACFUtil.simplifyString(color.toString())));
            }

            return colors.map(color -> ACFUtil.simplifyString(color.toString())).collect(Collectors.toList());
        });
        registerCompletion("players", c -> {
            CommandSender sender = c.getSender();
            Validate.notNull(sender, "Sender cannot be null");

            ArrayList<String> matchedPlayers = new ArrayList<>();
            for (Player player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                String name = player.getUsername();
                if (StringUtils.startsWithIgnoreCase(name, c.getInput())) {
                    matchedPlayers.add(name);
                }
            }

            matchedPlayers.sort(String.CASE_INSENSITIVE_ORDER);
            return matchedPlayers;
        });
    }
}
