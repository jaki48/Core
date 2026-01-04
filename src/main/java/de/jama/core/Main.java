package de.jama.core;

import de.jama.core.commands.*;
import de.jama.core.listeners.DeathMessageListener;
import de.jama.core.listeners.HomeListener;
import de.jama.core.utils.HomeManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private HomeManager homeManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        TpaCommand tpaCommand = new TpaCommand(this);
        this.homeManager = new HomeManager(this);

        getCommand("tpa").setExecutor(tpaCommand);
        getCommand("tpaccept").setExecutor(tpaCommand);
        getCommand("tpdeny").setExecutor(tpaCommand);

        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));

        getCommand("invsee").setExecutor(new InvSeeCommand(this));

        HomeCommand homeCmd = new HomeCommand(this, homeManager);
        getCommand("sethome").setExecutor(homeCmd);
        getCommand("home").setExecutor(homeCmd);

        getCommand("fly").setExecutor(new FlyCommand());

        getServer().getPluginManager().registerEvents(new DeathMessageListener(), this);

        getServer().getPluginManager().registerEvents(new HomeListener(this, homeManager), this);
    }

    @Override
    public void onDisable() {

    }
}
