package com.lostmc.lobby.menu;

import com.lostmc.bukkit.event.timer.ServerTimerEvent;
import com.lostmc.lobby.Lobby;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class MenuUpdaterListener implements Listener {

    private static final Map<UUID, MenuUpdateHandler> inventories = new HashMap<>();

    static {
        Bukkit.getPluginManager().registerEvents(new MenuUpdaterListener(), Lobby.getInstance());
    }

    public static void addUpdateHandler(Player p, MenuUpdateHandler handler) {
        synchronized (inventories) {
            inventories.put(p.getUniqueId(), handler);
        }
    }

    @EventHandler
    public void timer(ServerTimerEvent event) {
        if (event.getCurrentTick() % 20 != 0)
            return;
        Set<UUID> remove = Sets.newCopyOnWriteArraySet();
        for (Map.Entry<UUID, MenuUpdateHandler> entry : inventories.entrySet()) {
            Player next = Bukkit.getPlayer(entry.getKey());
            if (next == null || !next.isOnline() || next.getOpenInventory() == null) {
                remove.add(next.getUniqueId());
                continue;
            }
            entry.getValue().onUpdate(next);
        }
        for (UUID uuid : remove) {
            remove(uuid);
        }
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        remove(event.getPlayer().getUniqueId());
    }

    public void remove(UUID id) {
        synchronized (inventories) {
            inventories.remove(id);
        }
    }
}
