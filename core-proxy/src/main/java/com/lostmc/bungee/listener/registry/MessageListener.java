package com.lostmc.bungee.listener.registry;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lostmc.bungee.listener.ProxyListener;
import com.lostmc.bungee.manager.LoginManager;
import com.lostmc.bungee.server.BungeeProxyHandler;
import com.lostmc.core.Commons;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.server.ProxiedServer;
import com.lostmc.core.server.ServerType;
import com.lostmc.core.storage.account.AccountStorage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class MessageListener extends ProxyListener {

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getTag().equals("BungeeCord"))
            return;
        if (!(event.getSender() instanceof Server))
            return;
        if (!(event.getReceiver() instanceof ProxiedPlayer))
            return;
        ProxiedPlayer p = (ProxiedPlayer) event.getReceiver();
        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String subChannel = in.readUTF();
        switch (subChannel) {
            case "UpdateSingleData": {
                UUID id = UUID.fromString(in.readUTF());
                Profile profile = Profile.fromUniqueId(id);
                if (profile != null) {
                    try {
                        DataType dataType = DataType.valueOf(in.readUTF());
                        JsonObject object = Commons.getGson().fromJson(in.readUTF(), JsonObject.class);
                        JsonElement element = Commons.getGson().toJsonTree(object.get("data"));
                        profile.setDataElement(dataType, element);
                        Commons.getRedisBackend().saveRedisProfile(profile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case "REGISTRY_REQUEST": {
                Commons.getPlatform().runAsync(() -> {
                    try {
                        if (!p.getPendingConnection().isOnlineMode()) {
                            Profile tempProfile = Profile.getProfile(p);
                            AccountStorage storage = Commons.getStorageCommon().getAccountStorage();
                            Profile newProfile = storage.getProfile(p.getUniqueId());
                            if (newProfile == null)
                                newProfile = storage.createProfile(tempProfile.getUniqueId(),
                                        tempProfile.getName());
                            newProfile.setData(DataType.ACCOUNT_PASSWORD, in.readUTF());
                            newProfile.save();
                            Commons.getRedisBackend().saveSyncRedisProfile(newProfile);
                            LoginManager manager = getPlugin().getLoginManager();
                            manager.addToken(p.getAddress().getHostString(), newProfile.getName());
                            ProxiedServer server = ((BungeeProxyHandler) Commons.getProxyHandler())
                                    .getMostConnection(ServerType.MAIN_LOBBY);
                            if (server != null) {
                                ServerInfo info = ProxyServer.getInstance().getServerInfo(server.getId());
                                if (info != null) {
                                    p.connect(info);
                                    return;
                                }
                            }
                            p.disconnect(new TextComponent("§cServidores de lobby indisponíveis no momento" +
                                    "\nTente novamente mais tarde\nwww.lostmc.com.br"));
                        } else {
                            p.sendMessage(new TextComponent("§cVocê não precisa se autenticar."));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        p.disconnect(new TextComponent("§cFailed to registry:\n" + e));
                    }
                });
                break;
            }
            case "LOGIN_REQUEST": {
                if (!p.getPendingConnection().isOnlineMode()) {
                    Profile profile = Profile.getProfile(p);
                    LoginManager manager = getPlugin().getLoginManager();
                    manager.addToken(p.getAddress().getHostString(), profile.getName());
                    ProxiedServer server = ((BungeeProxyHandler) Commons.getProxyHandler())
                            .getMostConnection(ServerType.MAIN_LOBBY);
                    if (server != null) {
                        ServerInfo info = ProxyServer.getInstance().getServerInfo(server.getId());
                        if (info != null) {
                            p.connect(info);
                            return;
                        }
                    }
                    p.disconnect(new TextComponent("§cServidores de lobby indisponíveis no momento" +
                            "\nTente novamente mais tarde\nwww.lostmc.com.br"));
                } else {
                    p.sendMessage(new TextComponent("§cVocê não precisa se autenticar."));
                }
                break;
            }
            case "PLAY_HGMIX": {
                ProxiedServer server = ((BungeeProxyHandler) Commons.getProxyHandler()).getMostConnection(
                        ServerType.HUNGERGAMES);
                if (server != null) {
                    ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server.getId());
                    if (serverInfo != null) {
                        p.connect(serverInfo);
                    } else {
                        p.sendMessage(new TextComponent(ChatColor.RED + "INVALID_SERVER_INFO (" + server.getId() + ")"));
                    }
                } else {
                    p.sendMessage(new TextComponent(ChatColor.RED + "Nenhum servidor de hgmix encontrado."));
                }
                break;
            }
            case "REPLAY_HGMIX": {
                BungeeProxyHandler proxyHandler = ((BungeeProxyHandler) Commons.getProxyHandler());
                ProxiedServer server = proxyHandler.getMostConnection(ServerType.HUNGERGAMES);
                String message = null;
                if (server == null) {
                    message = "§cNenhum servidor de hgmix disponível, voce foi conectado ao lobby.";
                    if ((server = proxyHandler.getMostConnection(ServerType.HG_LOBBY)) == null) {
                        if ((server = proxyHandler.getMostConnection(ServerType.MAIN_LOBBY)) == null) {
                            p.disconnect(
                                    new TextComponent("§cNenhum servidor de hgmix ou lobby encontrado."));
                            return;
                        }
                    }
                }
                ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server.getId());
                if (serverInfo != null) {
                    p.connect(serverInfo);
                    if (message != null) {
                        p.sendMessage(new TextComponent(message));
                    }
                } else {
                    p.disconnect(new TextComponent("§cINVALID_SERVER_INFO (" + server.getId() + ")"));
                }
                break;
            }
            default:
                break;
        }
    }
}
