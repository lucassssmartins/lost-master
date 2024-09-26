package com.lostmc.lobby.server;

import com.lostmc.bukkit.control.Control;
import com.lostmc.bukkit.control.Controller;
import com.lostmc.bukkit.event.timer.ServerTimerEvent;
import com.lostmc.core.Commons;
import com.lostmc.core.server.ProxiedServer;
import com.lostmc.core.server.ServerType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.*;

public class ProxiedServerController extends Controller implements Listener {

    private Map<String, ProxiedServer> cache = new HashMap<>();

    public ProxiedServerController(Control control) {
        super(control);
    }

    @Override
    public void onEnable() {
        getServer().getScheduler().runTaskLater(getPlugin(), () -> registerListener(this), 5L);
    }

    public ProxiedServer getCachedServer(String serverId) {
        return this.cache.get(serverId);
    }

    public Collection<ProxiedServer> getCachedServers() {
        return cache.values();
    }

    public int getOnlineCount(ServerType serverType) {
        int count = 0;
        for (ProxiedServer server : this.cache.values()) {
            if (server.getServerType() != serverType)
                continue;
            count += server.getPlayers().size();
        }
        return count;
    }

    public List<ProxiedServer> getAvailableServers(ServerType serverType) {
        List<ProxiedServer> list = new ArrayList<>();
        for (ProxiedServer server : this.cache.values()) {
            if (server.getServerType() != serverType)
                continue;
            if (!server.isStarted() || server.isWhitelisted())
                continue;
            list.add(server);
        }
        return list;
    }

    public ProxiedServer getMostConnection(ServerType serverType) {
        ProxiedServer target = null;
        for (ProxiedServer available : getAvailableServers(serverType)) {
            if (target == null && available.canBeSelected()) {
                target = available;
                continue;
            }
            if (target.getPlayers().size() < available.getPlayers().size() && available.canBeSelected()) {
                target = available;
                continue;
            }
        }
        return target;
    }

    public int getHGOnlineCount() {
        return (getOnlineCount(ServerType.HUNGERGAMES));
    }

    public void get4sync() {
        Commons.getPlatform().runAsync(() -> {
           for (ProxiedServer server : Commons.getProxyHandler().getAllServers()) {
               if (server.isOutOfDate())
                   continue;
               cache.put(server.getId(), server);
           }
        });
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void timer(ServerTimerEvent event) {
        if (event.getCurrentTick() % 20 != 0)
            return;
        Iterator<ProxiedServer> it = cache.values().iterator();
        while (it.hasNext()) {
            ProxiedServer next = it.next();
            if (next.isOutOfDate()) {
                it.remove();
            }
        }
        get4sync();
    }
}
