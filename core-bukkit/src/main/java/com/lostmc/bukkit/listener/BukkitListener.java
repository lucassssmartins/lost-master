package com.lostmc.bukkit.listener;

import com.lostmc.bukkit.BukkitPlugin;
import org.bukkit.event.Listener;

public class BukkitListener implements Listener {

    private BukkitPlugin plugin;

    public void register(BukkitPlugin plugin) {
        (this.plugin = plugin).getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    public BukkitPlugin getPlugin() {
        return this.plugin;
    }
}
