package com.lostmc.bungee.listener;

import com.lostmc.bungee.ProxyPlugin;
import net.md_5.bungee.api.plugin.Listener;

public abstract class ProxyListener implements Listener {

    private ProxyPlugin plugin;

    public void register(ProxyPlugin plugin) {
        (this.plugin = plugin).getProxy().getPluginManager().registerListener(getPlugin(), this);
    }

    public ProxyPlugin getPlugin() {
        return this.plugin;
    }
}
