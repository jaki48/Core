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
                player.sendMessage(" §cNutze: /tpa <Spieler>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage("\uE006 §cSpieler nicht gefunden.");
                return true;
            }

            if (target == player) {
                player.sendMessage("\uE006 §cDu kannst dir selbst keine Anfrage schicken.");
                return true;
            }

            requests.put(target.getUniqueId(), player.getUniqueId());
            player.sendMessage(" §eTPA-Anfrage an §6" + target.getName() + " §egesendet.");

            // Klickbare Nachricht bauen
            TextComponent msg = new TextComponent("§6" + player.getName() + " §7möchte sich zu dir teleportieren.\n");

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
                player.sendMessage("\uE006 §cDu hast keine offenen Anfragen.");
                return true;
            }

            Player requester = Bukkit.getPlayer(requesterUUID);
            if (requester != null) {
                player.sendMessage("§aAnfrage angenommen.");
                requester.sendMessage("§a" + player.getName() + " hat deine Anfrage angenommen. Teleport startet...");

                TeleportUtil.teleportWithCountdown(requester, player.getLocation(), plugin);
            } else {
                player.sendMessage("§cDer Spieler ist nicht mehr online.");
            }

            requests.remove(player.getUniqueId());
            return true;
        }

        if (command.getName().equalsIgnoreCase("tpdeny")) {
            UUID requesterUUID = requests.remove(player.getUniqueId());
            if (requesterUUID == null) {
                player.sendMessage("\uE006 §cKeine offene Anfrage gefunden.");
                return true;
            }

            player.sendMessage("§cAnfrage abgelehnt.");
            Player requester = Bukkit.getPlayer(requesterUUID);
            if (requester != null) {
                requester.sendMessage("§6" + player.getName() + " §chat deine Anfrage abgelehnt.");
            }
            return true;
        }

        return false;
    }
}