package de.jama.core.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;

public class LPUtil {
    public static Component getFullDisplayName(Player player) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());

        if (user == null) {
            return Component.text(player.getName());
        }

        String prefix = user.getCachedData().getMetaData().getPrefix();

        if (prefix == null || prefix.isEmpty()) {
            return Component.text(player.getName());
        }

        String combined = prefix + player.getName();

        return LegacyComponentSerializer.legacyAmpersand().deserialize(combined);
    }
}