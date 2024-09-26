package com.lostmc.bungee.utils;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.lostmc.bungee.ProxyPlugin;
import com.lostmc.bungee.server.BungeeProxyHandler;
import com.lostmc.core.Commons;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.profile.tag.Tag;
import com.lostmc.core.server.ProxiedServer;
import com.lostmc.core.server.ServerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@AllArgsConstructor
@Getter
public class StaffMessage {

    private Profile sender;
    private String message;

    public void send() {
        String format = ChatColor.YELLOW + "[STAFF] " + Tag.fromRank(sender.getRank()).getPrefix()
                + sender.getName() + ": " + ChatColor.WHITE + message;
        for (ProxiedPlayer ps : ProxyServer.getInstance().getPlayers()) {
            if (ps.hasPermission("core.cmd.staffchat")) {
                Profile psP = Profile.getProfile(ps);
                if (psP != null) {
                    if (psP.getData(DataType.STAFF_CHAT_MESSAGES).getAsBoolean()) {
                        ProxiedServer connected = ((BungeeProxyHandler) Commons.getProxyHandler())
                                .getCachedServer(ps.getServer().getInfo().getName());
                        if (connected != null && connected.getServerType() != ServerType.AUTH) {
                            ps.sendMessage(new TextComponent(format));
                        }
                    }
                } else {
                    ps.disconnect(new TextComponent(ChatColor.RED + "ERR_PROFILE_NULL"));
                }
            }
        }
        WebhookMessageBuilder msg = new WebhookMessageBuilder();
        msg.setUsername(sender.getName());
        msg.setAvatarUrl("https://mc-heads.net/avatar/" + sender.getUniqueId().toString());
        msg.setContent(message);
        ProxyPlugin.getInstance().getStaffChatWebHook().send(msg.build());
        // TODO send discord message
    }
}
