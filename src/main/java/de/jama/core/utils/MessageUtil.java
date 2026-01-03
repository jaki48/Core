package de.jama.core.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class MessageUtil {

    private static final MiniMessage mm = MiniMessage.miniMessage();

    public static Component parse(String message) {
        return mm.deserialize(message);
    }

    public static void sendMessage(Player player, String message) {
        player.sendMessage(parse(message));
    }
}