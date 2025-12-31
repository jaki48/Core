package de.jama.core.commands;

import de.jama.core.Main;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class InvSeeCommand implements CommandExecutor {
    private final Main plugin;

    public InvSeeCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("core.admin.invsee")) {
            sender.sendMessage("§cPermission denied!");
            return true;
        }

        if(!(args.length == 1)) {
            sender.sendMessage("§cUsage: /invsee <player>");
            return true;
        }

        return true;
    }
}
