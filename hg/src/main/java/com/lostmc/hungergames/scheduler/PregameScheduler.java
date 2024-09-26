package com.lostmc.hungergames.scheduler;

import com.lostmc.game.manager.Management;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.constructor.ScheduleArgs;
import com.lostmc.hungergames.manager.GamerManager;
import com.lostmc.hungergames.stage.GameStage;
import com.lostmc.hungergames.util.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PregameScheduler implements Schedule {

    private boolean tpAll = false;

    @Override
    public void pulse(ScheduleArgs args) {
        if (!(args.getStage() == GameStage.PREGAME || args.getStage() == GameStage.STARTING))
            return;
        if (args.getTimer() <= 15 && args.getStage() == GameStage.PREGAME) {
			((HungerGames) HungerGames.getInstance()).setGameStage(GameStage.STARTING);
			((HungerGames) HungerGames.getInstance()).setTimer(args.getTimer());
        } else if (args.getTimer() <= 0) {
            if (((HungerGames) HungerGames.getInstance()).playersLeft() >= 1) {
                ((HungerGames) HungerGames.getInstance()).getGameMode().startGame();
            } else {
                tpAll = false;
                ((HungerGames) HungerGames.getInstance()).setGameStage(GameStage.WAITING);
            }
            return;
        }
        if (args.getTimer() <= 5) {
            if (!tpAll) {
                tpAll = true;
                Location spawn = HungerGames.getSpawnLocation();
                Queue<Player> tpQueue = new ConcurrentLinkedQueue<>(Bukkit.getOnlinePlayers());
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        if (tpQueue.isEmpty() || !tpAll)
                            cancel();
                        else {
                            tpQueue.poll().teleport(spawn);
                        }
                    }
                }.runTaskTimer(HungerGames.getInstance(), 1, 1);
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p.getLocation(), Sound.NOTE_STICKS, 1f, 1f);
            }
        }
        if ((args.getTimer() % 60 == 0 || (args.getTimer() < 60 && (args.getTimer() % 15 == 0 || args.getTimer() == 10 || args.getTimer() <= 5)))) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(MessageManager.getCountDownMessage(MessageManager.CountDownMessageType.GAMESTART, args.getTimer()));
            }
        }
    }
}
