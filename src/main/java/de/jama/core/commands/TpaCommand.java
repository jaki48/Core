package de.jama.core.commands;

import de.jama.core.Main;
import de.jama.core.utils.TeleportUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class TpaCommand implements CommandExecutor {

    private final Main plugin;
    private final HashMap<UUID, UUID> requests = new HashMap<>();

    public TpaCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("tpa")) {
            if (args.length != 1) {
                player.sendMessage("§8[§#ff8400Tᴘᴀ§8] §cɴᴜᴛᴢᴇ: /tpa <Spieler>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage("§8[§#ff8400Tᴘᴀ§8] §cѕᴘɪᴇʟᴇʀ ɴɪᴄʜᴛ ɢᴇꜰᴜɴᴅᴇɴ.");
                return true;
            }

            if (target == player) {
                player.sendMessage("§8[§#ff8400Tᴘᴀ§8] §cᴅᴜ ᴋᴀɴѕᴛ ᴅɪʀ ɴɪᴄʜᴛ ѕᴇʟʙѕᴛ ᴇɪɴᴇ ᴀɴꜰʀᴀɢᴇ ѕᴇɴᴅᴇɴ!");
                return true;
            }

            requests.put(target.getUniqueId(), player.getUniqueId());
            player.sendMessage("§8[§#ff8400Tᴘᴀ§8] §eᴛᴘᴀ ᴀɴꜰʀᴀɢᴇ ᴀɴ §6" + target.getName() + " §ɢᴇѕᴇɴᴅᴇᴛ!");

            // Klickbare Nachricht bauen
            TextComponent msg = new TextComponent("§8[§#ff8400Tᴘᴀ§8] §6" + player.getName() + " §7ᴍöᴄʜᴛᴇ ѕɪᴄʜ ᴢᴜ ᴅɪʀ ᴛᴇʟᴇᴘᴏʀᴛɪᴇʀᴇɴ\n");

            TextComponent accept = new TextComponent("§a§l[ANNEHMEN] ");
            accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));
            accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Klicke zum Akzeptieren")));

            TextComponent deny = new TextComponent("§c§l[ABLEHNEN]");
            deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));
            deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Klicke zum Ablehnen")));

            msg.addExtra(accept);
            msg.addExtra(deny);
            target.spigot().sendMessage(msg);
            return true;
        }

        // --- LOGIK FÜR /TPACCEPT ---
        if (command.getName().equalsIgnoreCase("tpaccept")) {
            UUID requesterUUID = requests.get(player.getUniqueId());
            if (requesterUUID == null) {
                player.sendMessage("§8[§#ff8400Tᴘᴀ§8]  §cᴅᴜ ʜᴀѕᴛ ᴋᴇɪɴᴇ ᴏꜰꜰᴇɴᴇɴ ᴀɴꜰʀᴀɢᴇɴ");
                return true;
            }

            Player requester = Bukkit.getPlayer(requesterUUID);
            if (requester != null) {
                player.sendMessage("§8[§#ff8400Tᴘᴀ§8] §aᴀɴꜰʀᴀɢᴇ ᴀɴɢᴇɴᴏᴍᴍᴇɴ!");
                requester.sendMessage("§8[§#ff8400Tᴘᴀ§8] §a" + player.getName() + " ʜᴀᴛ ᴅᴇɪɴᴇ ᴀɴꜰʀᴀɢᴇ ᴀɴɢᴇɴᴏᴍᴍᴇɴ.");

                TeleportUtil.teleportWithCountdown(requester, player.getLocation(), plugin);
            } else {
                player.sendMessage("§8[§#ff8400Tᴘᴀ§8] §cᴅᴇʀ ѕᴘɪᴇʟᴇʀ ɪѕᴛ ɴɪᴄʜᴛ ᴍᴇʜʀ ᴏɴʟɪɴᴇ.");
            }

            requests.remove(player.getUniqueId());
            return true;
        }

        if (command.getName().equalsIgnoreCase("tpdeny")) {
            UUID requesterUUID = requests.remove(player.getUniqueId());
            if (requesterUUID == null) {
                player.sendMessage("§8[§#ff8400Tᴘᴀ§8] §cᴅᴜ ʜᴀѕᴛ ᴋᴇɪɴᴇ ᴏꜰꜰᴇɴᴇɴ ᴀɴꜰʀᴀɢᴇɴ!");
                return true;
            }

            player.sendMessage("§cᴀɴꜰʀᴀɢᴇ ᴀʙɢᴇʟᴇʜɴᴛ.");
            Player requester = Bukkit.getPlayer(requesterUUID);
            if (requester != null) {
                requester.sendMessage("§6" + player.getName() + " §cʜᴀᴛ ᴅᴇɪɴᴇ ａɴꜰʀᴀɢᴇ ᴀʙɢᴇʟᴇʜɴᴛ.");
            }
            return true;
        }

        return false;
    }
}