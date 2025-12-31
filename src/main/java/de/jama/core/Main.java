package de.jama.core;

import de.jama.core.commands.InvSeeCommand;
import de.jama.core.commands.SetSpawnCommand;
import de.jama.core.commands.SpawnCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("invsee").setExecutor(new InvSeeCommand(this));
    }

    @Override
    public void onDisable() {

    }
}
