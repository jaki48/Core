package de.jama.core.listeners;

import de.jama.core.Main;
import de.jama.core.commands.HomeCommand;
import de.jama.core.utils.HomeManager;
import de.jama.core.utils.TeleportUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class HomeListener implements Listener {

    private final Main plugin;
    private final HomeManager homeManager;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public HomeListener(Main plugin, HomeManager homeManager) {
        this.plugin = plugin;
        this.homeManager = homeManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        String title = PlainTextComponentSerializer.plainText().serialize(event.getView().title());

        if (title.contains("Hᴏᴍᴇ")) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR || clicked.getType() == Material.GRAY_STAINED_GLASS_PANE) return;

            if (!clicked.hasItemMeta() || !clicked.getItemMeta().getPersistentDataContainer().has(HomeCommand.HOME_KEY, PersistentDataType.STRING)) return;

            String homeId = clicked.getItemMeta().getPersistentDataContainer().get(HomeCommand.HOME_KEY, PersistentDataType.STRING);

            if (clicked.getType() == Material.LIGHT_GRAY_BED) {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1f);
                return;
            }

            if (event.isLeftClick()) {
                Location loc = homeManager.getHome(player.getUniqueId(), homeId);
                if (loc != null) {
                    player.closeInventory();
                    TeleportUtil.teleportWithCountdown(player, loc, plugin);
                }
            } else if (event.isRightClick()) {
                openConfirmMenu(player, homeId);
            }
        }

        else if (title.contains("ʟöѕᴄʜᴇɴ")) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) return;

            String homeId = clickedItem.getItemMeta().getPersistentDataContainer().get(HomeCommand.HOME_KEY, PersistentDataType.STRING);
            if (homeId == null) return;

            if (clickedItem.getType() == Material.LIME_STAINED_GLASS_PANE) {
                homeManager.deleteHome(player.getUniqueId(), homeId);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
                player.sendMessage(mm.deserialize("<dark_gray>[<#0088ff>Hᴏᴍᴇ<dark_gray>] <green>ʜᴏᴍᴇ <yellow>" + homeId + " <green>ᴡᴜʀᴅᴇ ɢᴇʟöѕᴄʜᴛ."));

                player.closeInventory();
                player.performCommand("home");
            } else if (clickedItem.getType() == Material.RED_STAINED_GLASS_PANE) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                player.performCommand("home");
            }
        }
    }

    private void openConfirmMenu(Player player, String homeId) {
        Inventory inv = Bukkit.createInventory(null, 27, mm.deserialize("<#ff1100>ʟöѕᴄʜᴇɴ: <gray>" + homeId));

        ItemStack yes = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta yesMeta = yes.getItemMeta();
        yesMeta.displayName(mm.deserialize("<green>ʟöѕᴄʜᴇɴ ʙᴇѕᴛäᴛɪɢᴇɴ"));
        yesMeta.getPersistentDataContainer().set(HomeCommand.HOME_KEY, PersistentDataType.STRING, homeId);
        yes.setItemMeta(yesMeta);

        ItemStack no = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta noMeta = no.getItemMeta();
        noMeta.displayName(mm.deserialize("<#ff1100>ᴀʙʙʀᴇᴄʜᴇɴ"));
        noMeta.getPersistentDataContainer().set(HomeCommand.HOME_KEY, PersistentDataType.STRING, homeId);
        no.setItemMeta(noMeta);

        inv.setItem(11, yes);
        inv.setItem(15, no);

        player.openInventory(inv);
    }
}