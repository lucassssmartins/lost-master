package com.lostmc.bungee.server;

import com.google.common.collect.Sets;
import com.lostmc.bungee.ProxyPlugin;
import com.lostmc.core.Commons;
import com.lostmc.core.server.ProxiedServer;
import com.lostmc.core.server.ProxyHandler;
import com.lostmc.core.server.ServerType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class BungeeProxyHandler extends ProxyHandler {

    private final ProxyServer proxy;
    private Map<String, ProxiedServer> cache = new HashMap<>();

    public BungeeProxyHandler(ProxyServer proxy, ProxiedServer local) {
        super(local);
        this.proxy = proxy;
    }

    @Override
    public void startLocal() {
        super.startLocal();

        proxy.getScheduler().schedule(ProxyPlugin.getInstance(), () -> {
            Iterator<ProxiedServer> it = cache.values().iterator();
            while (it.hasNext()) {
                ProxiedServer next = it.next();
                if (next.isOutOfDate()) {
                    it.remove();
                }
            }
            proxy.getScheduler().runAsync(ProxyPlugin.getInstance(), () -> {
                for (ProxiedServer server : Commons.getProxyHandler().getAllServers()) {
                    if (server.isOutOfDate()) {
                        continue;
                    }
                    cache.put(server.getId(), server);
                }
            });
        }, 2, 2, TimeUnit.SECONDS);
    }

    public ProxiedServer getCachedServer(String serverId) {
        return cache.get(serverId);
    }

    public int getOnlineCount(ServerType serverType) {
        int count = 0;
        for (ProxiedServer server : cache.values()) {
            if (server.getServerType() != serverType)
                continue;
            count += server.getPlayers().size();
        }
        return count;
    }

    public List<ProxiedServer> getAvailableServers(ServerType serverType) {
        List<ProxiedServer> list = new ArrayList<>();
        for (ProxiedServer server : cache.values()) {
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
        List<ProxiedServer> list = getAvailableServers(serverType);
        list.sort(Comparator.comparing(ProxiedServer::getId));
        for (ProxiedServer available : list) {
            if (target == null && available.canBeSelected()) {
                target = available;
                continue;
            }
            if (target != null && target.getPlayers().size() < available.getPlayers().size() && available.canBeSelected()) {
                target = available;
                continue;
            }
        }
        return target;
    }

    public ServerInfo getServerInfo(String serverId) {
        return proxy.getServerInfo(serverId);
    }
}
