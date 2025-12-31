package de.jama.core.commands;

import de.jama.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import de.jama.core.utils.TeleportUtil;

public class SpawnCommand implements CommandExecutor {

    private final Main plugin;

    public SpawnCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (!player.hasPermission("core.spawn")) {
            player.sendMessage("§cKeine Rechte!");
            return true;
        }

        if (plugin.getConfig().get("spawn.world") == null) {
            player.sendMessage("§cEs wurde noch kein Spawn gesetzt!");
            return true;
        }

        World world = Bukkit.getWorld(plugin.getConfig().getString("spawn.world"));
        double x = plugin.getConfig().getDouble("spawn.x");
        double y = plugin.getConfig().getDouble("spawn.y");
        double z = plugin.getConfig().getDouble("spawn.z");
        float yaw = (float) plugin.getConfig().getDouble("spawn.yaw");
        float pitch = (float) plugin.getConfig().getDouble("spawn.pitch");

        Location spawnLoc = new Location(world, x, y, z, yaw, pitch);

        TeleportUtil.teleportWithCountdown(player, spawnLoc, plugin);
        player.sendMessage("§eWillkommen am Spawn!");

        return true;
    }
}