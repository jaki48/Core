package de.jama.core.commands;

import de.jama.core.Main;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Inventory;

public class InvSeeCommand implements CommandExecutor {
    private final Main plugin;

    public InvSeeCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if(!sender.hasPermission("core.admin.invsee")) {
            sender.sendMessage(" §cKeine Rechte!");
            return true;
        }

        if(!(args.length == 1)) {
            sender.sendMessage(" §cUsage: /invsee <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if(target == null) {
            sender.sendMessage(" §cPlayer not found!");
            return true;
        }

        PlayerInventory player_inf = target.getInventory();

        player.openInventory(player_inf);

        return true;
    }
}
