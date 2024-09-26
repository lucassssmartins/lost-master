package com.lostmc.pvp.listener;

import com.lostmc.bukkit.event.timer.ServerTimerEvent;
import com.lostmc.bukkit.listener.BukkitListener;
import com.lostmc.core.Commons;
import com.lostmc.core.server.ProxiedServer;
import com.lostmc.core.server.ServerType;
import org.bukkit.event.EventHandler;

public class TimerListener extends BukkitListener {

    public static int ONLINE_COUNT = 0;

    @EventHandler
    public void onServerTimerListener(ServerTimerEvent event) {
        if (event.getCurrentTick() % 40 == 0) {
            Commons.getPlatform().runAsync(() -> getOnlineCount());
        }
    }

    private int getOnlineCount() {
        int count = 0;
        for (ProxiedServer server : Commons.getProxyHandler().getServers(ServerType.PROXY))
            count += server.getPlayers().size();
        return ONLINE_COUNT = count;
    }
}
