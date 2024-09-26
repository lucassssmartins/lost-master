package com.lostmc.bukkit.listener.registry;

import com.lostmc.bukkit.event.server.ServerInfoUpdateEvent;
import com.lostmc.bukkit.event.timer.ServerTimerEvent;
import com.lostmc.bukkit.listener.BukkitListener;
import com.lostmc.core.Commons;
import com.lostmc.core.server.ProxyHandler;
import com.lostmc.core.server.ProxiedServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class ServerInfoListener extends BukkitListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTimer(ServerTimerEvent event) {
        if (event.getCurrentTick() % 40 != 0)
            return;

        ProxyHandler handler = Commons.getProxyHandler();
        ProxiedServer localServer = handler.getLocal();

        localServer.getPlayers().clear();
        for (Player o : Bukkit.getOnlinePlayers()) {
            localServer.getPlayers().put(o.getUniqueId(), o.getName());
        }

        localServer.setMaxPlayers(Bukkit.getMaxPlayers());
        localServer.setStarted(Commons.isSystemReady());
        localServer.setWhitelisted(Bukkit.hasWhitelist());

        ServerInfoUpdateEvent updateEvent = new ServerInfoUpdateEvent(localServer);
        Bukkit.getPluginManager().callEvent(updateEvent);

        handler.setLocal(updateEvent.getLocalhost());

        Commons.getPlatform().runAsync(handler::updateLocal);
    }
}
