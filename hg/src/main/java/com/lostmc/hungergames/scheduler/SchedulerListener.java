package com.lostmc.hungergames.scheduler;

import com.lostmc.bukkit.event.timer.ServerTimerEvent;
import com.lostmc.game.manager.Management;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.manager.SchedulerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SchedulerListener implements Listener {

    private HungerGames main;

    public SchedulerListener(HungerGames main) {
        this.main = main;
    }

    @EventHandler
    public void onServerTimerListener(ServerTimerEvent event) {
        if (event.getCurrentTick() % 20 != 0)
            return;
        Management.getManagement(SchedulerManager.class).pulse();
        main.count();
    }
}
