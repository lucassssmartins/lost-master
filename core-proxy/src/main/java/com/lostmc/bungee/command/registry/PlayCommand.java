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

public class PlayCommand extends WrappedProxyCommand {

    public PlayCommand() {
        super("play");
        setAliases("jogar");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) sender.getHandle();
            if (args.length == 0) {
                p.sendMessage(new TextComponent("§cUtilização do comando /play:"));
                p.sendMessage(new TextComponent("§c* /play <pvp>"));
                p.sendMessage(new TextComponent("§c* /play <hg>"));
            } else {
                BungeeProxyHandler proxyHandler = ((BungeeProxyHandler) Commons.getProxyHandler());
                ProxiedServer server;
                switch (args[0].toLowerCase()) {
                    case "pvp": {
                        if ((server = proxyHandler.getMostConnection(ServerType.PVP)) != null) {
                            ServerInfo info = getServerInfo(server);
                            if (info != null) {
                                p.connect(info);
                            } else {
                                p.sendMessage(new TextComponent("§cERROR: INVALID_INFO (" + server.getId() + ")"));
                            }
                        } else {
                            p.sendMessage(new TextComponent("§cNenhum servidor de pvp encontrado."));
                        }
                        break;
                    }
                    case "hg": {
                        if ((server = proxyHandler.getMostConnection(ServerType.HUNGERGAMES)) != null) {
                            ServerInfo info = getServerInfo(server);
                            if (info != null) {
                                p.connect(info);
                            } else {
                                p.sendMessage(new TextComponent("§cERROR: INVALID_INFO (" + server.getId() + ")"));
                            }
                        } else {
                            p.sendMessage(new TextComponent("§cNenhum servidor de hg encontrado."));
                        }
                        break;
                    }
                    default: {
                        p.sendMessage(new TextComponent("§cUtilização do comando /play:"));
                        p.sendMessage(new TextComponent("§c* /play <pvp>"));
                        p.sendMessage(new TextComponent("§c* /play <hg>"));
                        break;
                    }
                }
            }
        } else {
            sender.sendInGameMessage();
        }
    }

    public ServerInfo getServerInfo(ProxiedServer server) {
        return ProxyServer.getInstance().getServerInfo(server.getId());
    }
}
