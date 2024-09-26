package com.lostmc.hungergames.listener;

import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.constructor.HungerListener;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInitialSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class InitialSpawnListener extends HungerListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInitialSpawn(PlayerInitialSpawnEvent event) {
        if (getMain().getGameStage().isPregame()) {
            event.setSpawnLocation(HungerGames.getSpawnLocation());
        }
    }
}
