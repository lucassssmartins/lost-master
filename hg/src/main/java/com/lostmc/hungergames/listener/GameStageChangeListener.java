package com.lostmc.hungergames.listener;

import com.lostmc.game.manager.Management;
import com.lostmc.hungergames.constructor.HungerListener;
import com.lostmc.hungergames.event.game.GameStageChangeEvent;
import com.lostmc.hungergames.manager.SchedulerManager;
import com.lostmc.hungergames.scheduler.GameScheduler;
import com.lostmc.hungergames.scheduler.PregameScheduler;
import com.lostmc.hungergames.stage.GameStage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

public class GameStageChangeListener extends HungerListener {

    @EventHandler
    public void onGameStageChange(GameStageChangeEvent event) {
        if (event.getLastStage() == GameStage.WAITING) {
            if (event.getNewStage() == GameStage.PREGAME) {
                Management.getManagement(SchedulerManager.class).addScheduler("pregamer", new PregameScheduler());
            }
        } else if (event.getNewStage() == GameStage.WAITING) {
            Management.getManagement(SchedulerManager.class).cancelScheduler("pregamer");
            if (event.getLastStage() == GameStage.STARTING)
                Bukkit.broadcastMessage("§cNão há jogadores suficientes para iniciar a partida.");
        } else if (event.getLastStage() == GameStage.INVINCIBILITY) {
            SchedulerManager manager = Management.getManagement(SchedulerManager.class);
            manager.cancelScheduler("invincibility");
            manager.addScheduler("gametime", new GameScheduler());
            Bukkit.broadcastMessage("§cA invencibilidade acabou!");
        }
    }
}
