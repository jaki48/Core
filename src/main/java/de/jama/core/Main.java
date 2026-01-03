package de.jama.core;

import de.jama.core.commands.*;
import de.jama.core.listeners.DeathMessageListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        TpaCommand tpaCommand = new TpaCommand(this);

        getCommand("tpa").setExecutor(tpaCommand);
        getCommand("tpaccept").setExecutor(tpaCommand);
        getCommand("tpdeny").setExecutor(tpaCommand);

        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));

        getCommand("invsee").setExecutor(new InvSeeCommand(this));

        getServer().getPluginManager().registerEvents(new DeathMessageListener(), this);
    }

    @Override
    public void onDisable() {

    }
}
