package com.lostmc.bungee.command.registry;

import com.lostmc.bungee.command.WrappedProxyCommand;
import com.lostmc.bungee.server.BungeeProxyHandler;
import com.lostmc.core.Commons;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.server.ProxiedServer;
import com.lostmc.core.server.ServerType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class EventCommand extends WrappedProxyCommand {

    public EventCommand() {
        super("event");
        setAliases("evento");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) sender.getHandle();
            BungeeProxyHandler proxyHandler = ((BungeeProxyHandler) Commons.getProxyHandler());
            ProxiedServer server = proxyHandler.getMostConnection(ServerType.HG_EVENT);
            if (server != null) {
                ServerInfo info = getServerInfo(server);
                if (info != null) {
                    p.connect(info);
                } else {
                    p.sendMessage(new TextComponent("§cERROR: INVALID_INFO (" + server.getId() + ")"));
                }
            } else {
                p.sendMessage(new TextComponent("§cNenhum servidor disponível de evento encontrado."));
            }
        } else {
            sender.sendInGameMessage();
        }
    }

    public ServerInfo getServerInfo(ProxiedServer server) {
        return ProxyServer.getInstance().getServerInfo(server.getId());
    }
}
