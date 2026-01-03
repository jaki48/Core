package de.jama.core.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathMessageListener implements Listener {

    private final MiniMessage mm = MiniMessage.miniMessage();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Component vanillaMessage = event.deathMessage();
        if (vanillaMessage == null) return;
        Component prefix = mm.deserialize("<#ff1100>☠ ");
        event.deathMessage(prefix.append(vanillaMessage));
    }
}