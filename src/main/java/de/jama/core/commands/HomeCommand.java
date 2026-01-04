package de.jama.core.commands;

import de.jama.core.Main;
import de.jama.core.utils.HomeManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Set;

public class HomeCommand implements CommandExecutor {

    private final Main plugin;
    private final HomeManager homeManager;
    private final MiniMessage mm = MiniMessage.miniMessage();

    // Key um den Home-Namen im Item unsichtbar zu speichern
    public static final NamespacedKey HOME_KEY = new NamespacedKey("core", "homename");

    public HomeCommand(Main plugin, HomeManager homeManager) {
        this.plugin = plugin;
        this.homeManager = homeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("sethome")) {
            homeManager.setNextHome(player);
            return true;
        }

        if (command.getName().equalsIgnoreCase("home")) {
            int rows = homeManager.getInventoryRows(player);
            int maxHomes = homeManager.getMaxHomes(player);
            Set<String> existingHomes = homeManager.getHomeNames(player.getUniqueId());

            Inventory inv = Bukkit.createInventory(null, rows * 9, mm.deserialize("<dark_gray>Hᴏᴍᴇ"));

            for (int i = 1; i <= maxHomes; i++) {
                int slot = homeManager.getSlotForHome(i, player);
                if (slot == -1) continue;

                String id = String.valueOf(i);
                boolean isSet = existingHomes.contains(id);

                ItemStack item = new ItemStack(homeManager.getBedMaterial(i, isSet));
                ItemMeta meta = item.getItemMeta();

                if (isSet) {
                    meta.displayName(mm.deserialize("<#0088ff>ʜᴏᴍᴇ " + i).decoration(TextDecoration.ITALIC, false));
                    meta.lore(List.of(
                            mm.deserialize("<white>Linksklick: <green>Teleportieren").decoration(TextDecoration.ITALIC, false),
                            mm.deserialize("<white>Rechtsklick: <red>Löschen").decoration(TextDecoration.ITALIC, false)
                    ));
                } else {
                    meta.displayName(mm.deserialize("<gray>ɴɪᴄʜᴛ ɢᴇѕᴇᴛᴢᴛ").decoration(TextDecoration.ITALIC, false));
                }

                meta.getPersistentDataContainer().set(HOME_KEY, PersistentDataType.STRING, id);
                item.setItemMeta(meta);
                inv.setItem(slot, item);
            }

            player.openInventory(inv);
            return true;
        }
        return false;
    }
}