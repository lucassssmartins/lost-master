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

public class HubCommand extends WrappedProxyCommand {

    public HubCommand() {
        super("hub");
        setAliases("lobby", "l");
    }

    @Override
    public boolean runAsync() {
        return true;
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) sender.getHandle();
            BungeeProxyHandler proxyHandler = (BungeeProxyHandler) Commons.getProxyHandler();
            ServerInfo serverInfo = p.getServer().getInfo();
            ProxiedServer currentIn = proxyHandler.getServer(serverInfo.getName());
            ProxiedServer target;
            if (currentIn != null) {
                switch (currentIn.getServerType()) {
                    case PVP:
                    case TEST:
                    case UNDEFINED:
                    case HG_LOBBY:
                        target = proxyHandler.getMostConnection(ServerType.MAIN_LOBBY);
                        if (target != null) {
                            p.connect(ProxyServer.getInstance().getServerInfo(target.getId()));
                        } else {
                            sender.sendMessage("§cNenhum servidor de lobby encontrado!");
                        }
                        break;
                    case MAIN_LOBBY:
                        sender.sendMessage("§cVocê já está no lobby principal!");
                        break;
                    case HUNGERGAMES:
                    case HG_EVENT:
                        target = proxyHandler.getMostConnection(ServerType.HG_LOBBY);
                        if (target != null) {
                            p.connect(ProxyServer.getInstance().getServerInfo(target.getId()));
                        } else if ((target = proxyHandler.getMostConnection(ServerType.MAIN_LOBBY)) != null) {
                            p.connect(ProxyServer.getInstance().getServerInfo(target.getId()));
                            p.sendMessage(TextComponent.fromLegacyText("§cNenhum servidor de hg_lobby foi encontrado, " +
                                    "você foi conectado ao lobby principal."));
                        } else {
                            p.sendMessage(TextComponent.fromLegacyText("§cNenhum servidor de hg_lobby ou lobby principal encontrado!"));
                        }
                        break;
                    case PROXY:
                        break;
                    case AUTH:
                        if (p.getPendingConnection().isOnlineMode()) {
                            target = proxyHandler.getMostConnection(ServerType.MAIN_LOBBY);
                            if (target != null) {
                                p.connect(ProxyServer.getInstance().getServerInfo(target.getId()));
                            } else {
                                sender.sendMessage("§cNenhum servidor de lobby encontrado!");
                            }
                        }
                        break;
                    default:
                        break;
                }
            } else {
                target = proxyHandler.getMostConnection(ServerType.MAIN_LOBBY);
                if (target != null) {
                    p.connect(ProxyServer.getInstance().getServerInfo(target.getId()));
                } else {
                    sender.sendMessage("§cNenhum servidor de lobby encontrado!");
                }
            }
        } else {
            sender.sendInGameMessage();
        }
    }
}
