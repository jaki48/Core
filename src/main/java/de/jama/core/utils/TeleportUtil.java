package de.jama.core.utils;

import de.jama.core.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashMap;
import java.util.UUID;

public class TeleportUtil {

    public static final HashMap<UUID, Integer> pendingTeleports = new HashMap<>();

    public static void teleportWithCountdown(Player player, Location target, Main plugin) {
        UUID uuid = player.getUniqueId();

        if (pendingTeleports.containsKey(uuid)) {
            player.sendMessage("§cDu wirst bereits teleportiert!");
            return;
        }

        Location startLoc = player.getLocation();

        int taskId = new BukkitRunnable() {
            int seconds = 3;

            @Override
            public void run() {
                if (player.getLocation().getBlockX() != startLoc.getBlockX() ||
                        player.getLocation().getBlockZ() != startLoc.getBlockZ()) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            new TextComponent("§cTeleport abgebrochen"));
                    pendingTeleports.remove(uuid);
                    this.cancel();
                    return;
                }

                if (seconds > 0) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            new TextComponent("§7Teleport in: " + seconds));
                    seconds--;
                } else {
                    player.teleport(target);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            new TextComponent("§aTeleportiert!"));
                    pendingTeleports.remove(uuid);
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L).getTaskId();

        pendingTeleports.put(uuid, taskId);
    }
}