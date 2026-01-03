package de.jama.core.commands;

import de.jama.core.Main;
import de.jama.core.utils.TeleportUtil;
import de.jama.core.utils.LPUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class TpaCommand implements CommandExecutor {

    private final Main plugin;

    private final HashMap<UUID, TpaRequest> requests = new HashMap<>();
    private final HashMap<UUID, UUID> pendingOutgoing = new HashMap<>();

    private final String prefixStr = "<dark_gray>[<color:#ff8400>Tᴘᴀ</color><dark_gray>] ";
    private final long TIMEOUT_MS = 2 * 60 * 1000; // 2 Minuten

    public TpaCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("tpa")) {
            if (pendingOutgoing.containsKey(player.getUniqueId())) {
                UUID currentTargetUUID = pendingOutgoing.get(player.getUniqueId());
                TpaRequest currentReq = requests.get(currentTargetUUID);

                if (currentReq != null && !currentReq.isExpired(TIMEOUT_MS)) {
                    send(player, prefixStr + "<red>ᴅᴜ ʜᴀѕᴛ ʙᴇʀᴇɪᴛѕ ᴇɪɴᴇ ᴏꜰꜰᴇɴᴇ ᴀɴꜰʀᴀɢᴇ!");
                    return true;
                } else {
                    requests.remove(currentTargetUUID);
                    pendingOutgoing.remove(player.getUniqueId());
                }
            }

            if (args.length != 1) {
                send(player, prefixStr + "<red>ɴᴜᴛᴢᴇ: /tpa <Spieler>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                send(player, prefixStr + "<red>ѕᴘɪᴇʟᴇʀ ɴɪᴄʜᴛ ɢᴇꜰᴜɴᴅᴇɴ.");
                return true;
            }

            if (target == player) {
                send(player, prefixStr + "<red>ᴅᴜ ᴋᴀɴѕᴛ ᴅɪʀ ɴɪᴄʜᴛ ѕᴇʟʙѕᴛ ᴇɪɴᴇ ᴀɴꜰʀᴀɢᴇ ѕᴇɴᴅᴇɴ!");
                return true;
            }

            requests.put(target.getUniqueId(), new TpaRequest(player.getUniqueId(), System.currentTimeMillis()));
            pendingOutgoing.put(player.getUniqueId(), target.getUniqueId());

            player.sendMessage(mm(prefixStr + "<yellow>ᴛᴘᴀ ᴀɴꜰʀᴀɢᴇ ᴀɴ ")
                    .append(LPUtil.getFullDisplayName(target))
                    .append(mm(" <yellow>ɢᴇѕᴇɴᴅᴇᴛ!")));

            Component requestMsg = mm(prefixStr)
                    .append(LPUtil.getFullDisplayName(player))
                    .append(mm(" <gray>ᴍöᴄʜᴛᴇ ѕɪᴄʜ ᴢᴜ ᴅɪʀ ᴛᴇʟᴇᴘᴏʀᴛɪᴇʀᴇɴ\n"))
                    .append(mm("<click:run_command:'/tpaccept'><hover:show_text:'Annehmen'><green><bold>[ANNEHMEN]</bold></hover></click> "))
                    .append(mm("<click:run_command:'/tpdeny'><hover:show_text:'Ablehnen'><red><bold>[ABLEHNEN]</bold></hover></click>"));

            target.sendMessage(requestMsg);
            return true;
        }

        if (command.getName().equalsIgnoreCase("tpaccept")) {
            TpaRequest req = requests.get(player.getUniqueId());

            if (req == null || req.isExpired(TIMEOUT_MS)) {
                requests.remove(player.getUniqueId());
                send(player, prefixStr + "<red>ᴅᴜ ʜᴀѕᴛ ᴋᴇɪɴᴇ ᴏꜰꜰᴇɴᴇɴ ᴏᴅᴇʀ ᴀᴋᴛᴜᴇʟʟᴇɴ ᴀɴꜰʀᴀɢᴇɴ.");
                return true;
            }

            Player requester = Bukkit.getPlayer(req.requesterId);
            if (requester != null) {
                send(player, prefixStr + "<green>ᴀɴꜰʀᴀɢᴇ ᴀɴɢᴇɴᴏᴍᴍᴇɴ!");
                requester.sendMessage(mm(prefixStr).append(LPUtil.getFullDisplayName(player)).append(mm(" <green>ʜᴀᴛ ᴅɪᴇ ᴀɴꜰʀᴀɢᴇ ᴀɴɢᴇɴᴏᴍᴍᴇɴ.")));

                new org.bukkit.scheduler.BukkitRunnable() {
                    org.bukkit.Location lastLoc = requester.getLocation();
                    boolean firstRun = true;

                    @Override
                    public void run() {
                        if (!requester.isOnline()) {
                            this.cancel();
                            return;
                        }

                        if (firstRun) {
                            lastLoc = requester.getLocation();
                            firstRun = false;
                            return;
                        }

                        org.bukkit.Location currentLoc = requester.getLocation();

                        if (lastLoc.getWorld() != currentLoc.getWorld() || lastLoc.distance(currentLoc) > 0.1) {
                            requester.sendActionBar(mm("<gray>Bitte bleibe stehen..."));

                            lastLoc = currentLoc;
                        } else {
                            TeleportUtil.teleportWithCountdown(requester, player, plugin);
                            this.cancel();
                        }
                    }
                }.runTaskTimer(plugin, 0L, 5L);
            }

            pendingOutgoing.remove(req.requesterId);
            requests.remove(player.getUniqueId());
            return true;
        }

        if (command.getName().equalsIgnoreCase("tpdeny")) {
            TpaRequest req = requests.remove(player.getUniqueId());
            if (req == null || req.isExpired(TIMEOUT_MS)) {
                send(player, prefixStr + "<red>ᴋᴇɪɴᴇ ᴀᴋᴛɪᴠᴇ ᴀɴꜰʀᴀɢᴇ ɢᴇꜰᴜɴᴅᴇɴ.");
                return true;
            }

            send(player, prefixStr + "<red>ᴀɴꜰʀᴀɢᴇ ᴀʙɢᴇʟᴇʜɴᴛ.");
            Player requester = Bukkit.getPlayer(req.requesterId);
            if (requester != null) {
                requester.sendMessage(mm(prefixStr).append(LPUtil.getFullDisplayName(player)).append(mm(" <red>ʜᴀᴛ ᴀʙɢᴇʟᴇʜɴᴛ.")));
            }
            pendingOutgoing.remove(req.requesterId);
            return true;
        }

        return false;
    }

    private Component mm(String text) { return MiniMessage.miniMessage().deserialize(text); }
    private void send(Player player, String message) { player.sendMessage(mm(message)); }

    private static class TpaRequest {
        final UUID requesterId;
        final long timestamp;

        TpaRequest(UUID requesterId, long timestamp) {
            this.requesterId = requesterId;
            this.timestamp = timestamp;
        }

        boolean isExpired(long timeoutMs) {
            return (System.currentTimeMillis() - timestamp) > timeoutMs;
        }
    }
}