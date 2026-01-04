package de.jama.core.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FlyCommand implements CommandExecutor {

    private final MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur Spieler können fliegen.");
            return true;
        }

        if (!player.hasPermission("core.admin.fly")) {
            player.sendMessage(mm.deserialize("<red>Dazu hast du keine Rechte."));
            return true;
        }

        // Flugstatus umkehren
        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.setFlying(false);
            player.sendMessage(mm.deserialize("<dark_gray>[<#0088ff>Fʟʏ<dark_gray>] <red>ꜰʟᴜɢᴍᴏᴅᴜѕ ᴅᴇᴀᴋᴛɪᴠɪᴇʀᴛ"));
        } else {
            player.setAllowFlight(true);
            player.sendMessage(mm.deserialize("<dark_gray>[<#0088ff>Fʟʏ<dark_gray>] <green>ꜰʟᴜɢᴍᴏᴅᴜѕ ᴀᴋᴛɪᴠɪᴇʀᴛ"));
        }

        return true;
    }
}