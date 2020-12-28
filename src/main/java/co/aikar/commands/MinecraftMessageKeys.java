package co.aikar.commands;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

import java.util.Locale;

public enum MinecraftMessageKeys implements MessageKeyProvider {

    INVALID_WORLD,
    YOU_MUST_BE_HOLDING_ITEM,
    PLAYER_IS_VANISHED_CONFIRM,
    USERNAME_TOO_SHORT,
    IS_NOT_A_VALID_NAME,
    MULTIPLE_PLAYERS_MATCH,
    NO_PLAYER_FOUND_SERVER,
    NO_PLAYER_FOUND_OFFLINE,
    NO_PLAYER_FOUND,
    LOCATION_PLEASE_SPECIFY_WORLD,
    LOCATION_PLEASE_SPECIFY_XYZ,
    LOCATION_CONSOLE_NOT_RELATIVE;

    private final MessageKey key = MessageKey.of("acf-minecraft." + this.name().toLowerCase(Locale.ENGLISH));
    public MessageKey getMessageKey() {
        return key;
    }

}
