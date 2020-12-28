package co.aikar.commands;

import net.minestom.server.chat.ChatColor;

public class MinestomMessageFormatter extends MessageFormatter<ChatColor> {

    public MinestomMessageFormatter(ChatColor... colors) {
        super(colors);
    }

    @Override
    String format(ChatColor color, String message) {
        return color + message;
    }

}
