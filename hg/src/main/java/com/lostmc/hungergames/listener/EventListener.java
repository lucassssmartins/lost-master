package com.lostmc.hungergames.listener;

import com.lostmc.bukkit.event.vanish.PlayerVanishEvent;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.constructor.HungerListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener extends HungerListener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        getMain().checkTimer();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeave(PlayerQuitEvent event) {
        getMain().checkTimer();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAdminMode(PlayerVanishEvent event) {
        getMain().checkTimer();
    }
}
