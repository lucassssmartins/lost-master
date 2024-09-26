package com.lostmc.bungee.listener.registry;

import com.lostmc.bungee.ProxyPlugin;
import com.lostmc.bungee.listener.ProxyListener;
import com.lostmc.bungee.server.BungeeProxyHandler;
import com.lostmc.core.Commons;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.server.ProxiedServer;
import com.lostmc.core.server.ServerType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ServerConnectListener extends ProxyListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onConnect(ServerConnectEvent event) {
        ProxiedPlayer p = event.getPlayer();
        ServerInfo info = event.getTarget();
        ProxiedServer server = ((BungeeProxyHandler) Commons.getProxyHandler())
                .getCachedServer(info.getName());
        if (!p.getPendingConnection().isOnlineMode()) {
            if (server != null) {
                if (server.getServerType() != ServerType.AUTH) {
                    if (!getPlugin().getLoginManager().hasToken(p.getAddress().getHostString(),
                            Profile.getProfile(p).getName())) {
                        ProxiedServer authServer = ((BungeeProxyHandler) Commons.getProxyHandler())
                                .getMostConnection(ServerType.AUTH);
                        if (authServer != null) {
                            ServerInfo target = ProxyServer.getInstance().getServerInfo(authServer.getId());
                            if (target != null) {
                                event.setTarget(target);
                            } else {
                                p.disconnect("§cServidores de login indisponíveis\nTente novamente mais tarde");
                            }
                        } else {
                            p.disconnect("§cServidores de login indisponíveis\nTente novamente mais tarde");
                        }
                    }
                }
            } else if (p.getServer() != null) {
                event.setCancelled(true);
                p.sendMessage(TextComponent.fromLegacyText("§cTarget server not updated."));
            } else {
                p.disconnect(TextComponent.fromLegacyText("§cTarget server not updated (" + info.getName().toUpperCase() + ")"));
            }
        }
    }
}
