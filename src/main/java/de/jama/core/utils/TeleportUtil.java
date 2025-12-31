package de.jama.core.utils;

import de.jama.core.Main;
import org.bukkit.Location;
import org.bukkit.Sound;
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
            player.sendMessage(" §cDu wirst bereits teleportiert!");
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
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5f, 1.0f);
                    pendingTeleports.remove(uuid);
                    this.cancel();
                    return;
                }

                if (seconds > 0) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            new TextComponent("§7Teleport in: §6" + seconds));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 0.1f, 1.0f);
                    seconds--;
                } else {
                    player.teleport(target);
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 2.0f);
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