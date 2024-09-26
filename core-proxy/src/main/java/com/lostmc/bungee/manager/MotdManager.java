package com.lostmc.bungee.manager;

import com.lostmc.bungee.ProxyPlugin;
import com.lostmc.bungee.utils.Chat;
import com.google.common.base.Preconditions;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class MotdManager extends ProxyManager implements Listener {

    private String defaultHeaderMotd;
    private String defaultFooterMotd;

    public MotdManager(final ProxyPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        {
            saveDefaultHeaderMotd("§c-§6-§e-§a-§b- §lLOST §b-§a-§e-§6-§c-");
        }

        {
           saveDefaultFooterMotd("§d§lA NEW HISTORY BEGINS");
        }

        registerListener(this);
    }

    public void saveDefaultHeaderMotd(String motd) {
        Preconditions.checkNotNull(motd, "motd cannot be null");

        getPlugin().runAsync(() -> {
            getPlugin().getConfig().set("proxy.motd.default-header", defaultHeaderMotd = motd);
            getPlugin().saveConfig();
        });
    }

    public void saveDefaultFooterMotd(String motd) {
        Preconditions.checkNotNull(motd, "motd cannot be null");

        getPlugin().runAsync(() -> {
            getPlugin().getConfig().set("proxy.motd.default-footer", defaultFooterMotd = motd);
            getPlugin().saveConfig();
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void ping(ProxyPingEvent event) {
        event.setResponse(new ServerPing(event.getResponse().getVersion(),
                new ServerPing.Players(getProxy().getConfig().getPlayerLimit(), getProxy().getOnlineCount(),
                        new ServerPing.PlayerInfo[] { new ServerPing.PlayerInfo("", "") }),
                Chat.makeCenteredMotd(defaultHeaderMotd) + "\n" + (getPlugin().isMaintenance() ?
                        Chat.makeCenteredMotd("§cEstamos em manutenção, voltamos já!") :
                        Chat.makeCenteredMotd(defaultFooterMotd)),
                event.getResponse().getFaviconObject()));
    }

    @Override
    public void onDisable() {

    }
}
