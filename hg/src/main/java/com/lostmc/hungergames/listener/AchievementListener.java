package com.lostmc.hungergames.listener;

import com.lostmc.hungergames.constructor.HungerListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;

public class AchievementListener extends HungerListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAchievementAwarded(PlayerAchievementAwardedEvent event) {
        event.setCancelled(true);
    }
}
