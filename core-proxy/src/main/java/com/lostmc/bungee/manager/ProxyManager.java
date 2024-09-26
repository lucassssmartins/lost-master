package com.lostmc.bungee.manager;

import com.lostmc.bungee.ProxyPlugin;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;

public abstract class ProxyManager {

    @Getter
    private final ProxyPlugin plugin;

    public ProxyManager(final ProxyPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract void onEnable();

    public abstract void onDisable();

    public ProxyServer getProxy() {
        return getPlugin().getProxy();
    }

    public void registerListener(Listener listener) {
        getProxy().getPluginManager().registerListener(getPlugin(), listener);
    }
}
