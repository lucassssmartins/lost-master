package com.lostmc.bungee.listener.registry;

import com.lostmc.bungee.listener.ProxyListener;
import com.lostmc.core.Commons;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class DisconnectListener extends ProxyListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDisconnect(PlayerDisconnectEvent event) {
        getPlugin().getSilentManager().removeSilent(event.getPlayer().getUniqueId());
        Commons.getProfileMap().remove(event.getPlayer().getUniqueId());
        Commons.getRedisBackend().removeRedisProfile(event.getPlayer().getUniqueId());
    }
}
