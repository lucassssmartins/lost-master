package com.lostmc.bungee.listener.registry;

import com.lostmc.bungee.listener.ProxyListener;
import com.lostmc.bungee.server.BungeeProxyHandler;
import com.lostmc.bungee.utils.StaffMessage;
import com.lostmc.core.Commons;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.punishment.SilentPunishment;
import com.lostmc.core.server.ProxiedServer;
import com.lostmc.core.server.ServerType;
import com.lostmc.core.utils.DateUtils;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.event.EventHandler;

public class ChatListener extends ProxyListener {

    @EventHandler
    public void onChat(ChatEvent event) {
        if (event.isCommand())
            return;
        if (event.isCancelled())
            return;
        if (!(event.getSender() instanceof ProxiedPlayer))
            return;
        ProxiedPlayer p = (ProxiedPlayer) event.getSender();
        Profile profile = Profile.getProfile(p);
        SilentPunishment silent = getPlugin().getSilentManager().getSilent(p.getUniqueId());
        if (silent != null) {
            if (!silent.isLifetime()) {
                if (!silent.isExpired()) {
                    event.setCancelled(true);
                    p.sendMessage(new TextComponent("§cVocê está mutado por " + silent.getReason() + ", expira em "
                            + DateUtils.getTime(silent.getExpiresIn())));
                } else {
                    getPlugin().getSilentManager().removeSilent(p.getUniqueId());
                    Commons.getPlatform().runAsync(() -> {
                       try {
                           Commons.getStorageCommon().getPunishmentStorage().pardonPunishment(silent.getId());
                       } catch (Exception e) {
                           e.printStackTrace();
                       }
                    });
                }
            } else {
                event.setCancelled(true);
                p.sendMessage(new TextComponent("§cVocê está mutado permanentemente por " + silent.getReason()));
                p.sendMessage(new TextComponent("§cAdquira unmute em loja.lostmc.com.br"));
            }
        }
        if (!event.isCancelled()) {
            if (profile.getData(DataType.STAFF_CHAT_MODE).getAsBoolean()) {
                if (p.hasPermission("core.cmd.staffchat")) {
                    if (profile.getData(DataType.STAFF_CHAT_MESSAGES).getAsBoolean()) {
                        event.setCancelled(true);
                        ServerInfo serverInfo = p.getServer().getInfo();
                        ProxiedServer proxiedServer = ((BungeeProxyHandler) Commons.getProxyHandler())
                                .getCachedServer(serverInfo.getName());
                        if (proxiedServer != null) {
                            if (proxiedServer.getServerType() != ServerType.AUTH) {
                                new StaffMessage(profile, event.getMessage()).send();
                            } else {
                                p.sendMessage(TextComponent.fromLegacyText("§cINVALID_REQUEST (AUTH SERVER CONNECTED)"));
                            }
                        } else {
                            p.sendMessage(TextComponent.fromLegacyText("§cINVALID_SERVER_CONNECTED (" + serverInfo.getName() + ")"));
                        }
                    } else {
                        event.setCancelled(true);
                        p.sendMessage(TextComponent.fromLegacyText("§cMensagens do bate-papo da equipe desativadas!"));
                    }
                } else {
                    profile.setData(DataType.STAFF_CHAT_MODE, false);
                    profile.save();
                    Commons.getRedisBackend().saveRedisProfile(profile);
                }
            }
        }
    }
}
