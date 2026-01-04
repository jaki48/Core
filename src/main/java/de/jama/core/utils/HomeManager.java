package de.jama.core.utils;

import de.jama.core.Main;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class HomeManager {

    private final Main plugin;
    private File file;
    private FileConfiguration config;

    public HomeManager(Main plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "homes.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void setHome(Player player, String name) {
        String path = player.getUniqueId() + "." + name;
        Location loc = player.getLocation();

        // Block unter dem Spieler als Icon nutzen (oder GRASS_BLOCK wenn Luft)
        Material icon = loc.getBlock().getRelative(BlockFace.DOWN).getType();
        if (icon.isAir()) icon = Material.GRASS_BLOCK;

        config.set(path + ".world", loc.getWorld().getName());
        config.set(path + ".x", loc.getX());
        config.set(path + ".y", loc.getY());
        config.set(path + ".z", loc.getZ());
        config.set(path + ".yaw", loc.getYaw());
        config.set(path + ".pitch", loc.getPitch());
        config.set(path + ".icon", icon.name());
        save();
    }

    public Location getHome(UUID uuid, String name) {
        String path = uuid + "." + name;
        if (!config.contains(path)) return null;

        return new Location(
                Bukkit.getWorld(config.getString(path + ".world")),
                config.getDouble(path + ".x"),
                config.getDouble(path + ".y"),
                config.getDouble(path + ".z"),
                (float) config.getDouble(path + ".yaw"),
                (float) config.getDouble(path + ".pitch")
        );
    }

    public Material getIcon(UUID uuid, String name) {
        String matName = config.getString(uuid + "." + name + ".icon", "GRASS_BLOCK");
        return Material.getMaterial(matName);
    }

    public void deleteHome(UUID uuid, String name) {
        if (name == null || name.isEmpty()) return; // Sicherheitscheck
        config.set(uuid.toString() + "." + name, null);
        save();
    }

    public Set<String> getHomeNames(UUID uuid) {
        if (!config.contains(uuid.toString())) return Set.of();
        return config.getConfigurationSection(uuid.toString()).getKeys(false);
    }

    public int getHomeCount(Player player) {
        return getHomeNames(player.getUniqueId()).size();
    }

    public int getMaxHomes(Player player) {
        if (player.hasPermission("core.home.vip")) return 10;
        if (player.hasPermission("core.home.default")) return 3;
        return 0; // Keine Rechte
    }
    public int getSlotForHome(int homeNumber, Player player) {
        if (player.hasPermission("core.home.vip")) {
            // VIP Slots: 10, 11, 12, 13, 14, 15, 16, 19, 20, 21 (für 10 Homes)
            int[] vipSlots = {10, 11, 12, 13, 14, 15, 16, 21, 22, 23};
            return (homeNumber <= vipSlots.length) ? vipSlots[homeNumber - 1] : -1;
        } else {
            // Default Slots: 12, 13, 14
            int[] defaultSlots = {12, 13, 14};
            return (homeNumber <= defaultSlots.length) ? defaultSlots[homeNumber - 1] : -1;
        }
    }
    public Material getBedMaterial(int homeNumber, boolean isSet) {
        if (!isSet) return Material.LIGHT_GRAY_BED;

        return switch (homeNumber) {
            case 1 -> Material.RED_BED;
            case 2 -> Material.ORANGE_BED;
            case 3 -> Material.YELLOW_BED;
            case 4 -> Material.LIME_BED;
            case 5 -> Material.GREEN_BED;
            case 6 -> Material.CYAN_BED;
            case 7 -> Material.LIGHT_BLUE_BED;
            case 8 -> Material.BLUE_BED;
            case 9 -> Material.PURPLE_BED;
            case 10 -> Material.MAGENTA_BED;
            default -> Material.WHITE_BED;
        };
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setNextHome(Player player) {
        int max = getMaxHomes(player);
        Set<String> existing = getHomeNames(player.getUniqueId());

        // Suche die erste freie Nummer
        for (int i = 1; i <= max; i++) {
            if (!existing.contains(String.valueOf(i))) {
                setHome(player, String.valueOf(i));
                player.sendMessage(MiniMessage.miniMessage().deserialize("<dark_gray>[<#0088ff>Hᴏᴍᴇ<dark_gray>] <green>ʜᴏᴍᴇ " + i + " ᴡᴜʀᴅᴇ ɢᴇѕᴛᴇᴛᴢᴛ!"));
                return;
            }
        }
        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>ᴅᴜ ʜᴀѕᴛ ᴀʟʟᴇ ᴅᴇɪɴᴇ <yellow>" + max + " <red>ʜᴏᴍᴇѕ ʙᴇʀᴇɪᴛѕ ʙᴇʟᴇɢᴛ!"));
    }

    public int getInventoryRows(Player player) {
        if (player.hasPermission("core.home.vip")) return 4; // 4 Reihen (36 Slots)
        return 3; // Standard: 3 Reihen (27 Slots)
    }
}