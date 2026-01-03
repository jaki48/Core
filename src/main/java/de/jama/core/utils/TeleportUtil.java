package de.jama.core.utils;

import de.jama.core.Main;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class TeleportUtil {

    public static final HashMap<UUID, Integer> pendingTeleports = new HashMap<>();
    private static final MiniMessage mm = MiniMessage.miniMessage();

    public static void teleportWithCountdown(Player player, Location target, Main plugin) {
        startCountdown(player, target, null, plugin);
    }

    public static void teleportWithCountdown(Player player, Player targetPlayer, Main plugin) {
        startCountdown(player, null, targetPlayer, plugin);
    }

    private static void startCountdown(Player player, Location staticTarget, Player dynamicTarget, Main plugin) {
        UUID uuid = player.getUniqueId();
        if (pendingTeleports.containsKey(uuid)) return;

        Location startLoc = player.getLocation();

        int taskId = new BukkitRunnable() {
            int seconds = 3;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    pendingTeleports.remove(uuid);
                    this.cancel();
                    return;
                }

                if (dynamicTarget != null && !dynamicTarget.isOnline()) {
                    player.sendMessage(mm.deserialize("<red>Teleport abgebrochen! Das Ziel hat den Server verlassen."));
                    player.sendActionBar(mm.deserialize("<color:#ff1100>Ziel offline"));
                    pendingTeleports.remove(uuid);
                    this.cancel();
                    return;
                }

                if (player.getLocation().getBlockX() != startLoc.getBlockX() ||
                        player.getLocation().getBlockZ() != startLoc.getBlockZ()) {
                    player.sendActionBar(mm.deserialize("<color:#ff1100>Teleport abgebrochen"));
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5f, 1.0f);
                    pendingTeleports.remove(uuid);
                    this.cancel();
                    return;
                }

                if (seconds > 0) {
                    player.sendActionBar(mm.deserialize("<gray>Teleport in: <color:#ff8400>" + seconds));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 0.1f, 1.0f);
                    seconds--;
                } else {
                    if (dynamicTarget != null) {
                        player.teleport(dynamicTarget.getLocation());
                    } else {
                        player.teleport(staticTarget);
                    }

                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 2.0f);
                    player.sendActionBar(mm.deserialize("<green>Teleportiert!"));
                    pendingTeleports.remove(uuid);
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L).getTaskId();

        pendingTeleports.put(uuid, taskId);
    }
}