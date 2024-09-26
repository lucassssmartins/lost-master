package com.lostmc.bukkit.npc.listeners;

import com.lostmc.bukkit.event.timer.ServerTimerEvent;
import com.lostmc.bukkit.npc.NpcController;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

@Getter
public class PeriodicMoveListener extends HandleMoveBase implements Listener {

    private final NpcController instance;
    private final long updateInterval;

    private final HashMap<UUID, BukkitTask> tasks = new HashMap<>();

    public PeriodicMoveListener(NpcController instance, long updateInterval) {
        this.instance = instance;
        this.updateInterval = updateInterval;
    }

    private void startTask(UUID uuid) {}

    @EventHandler
    public void timer(ServerTimerEvent event) {
        if (event.getCurrentTick() % 1 != 0)
            return;
        for (Player p : Bukkit.getOnlinePlayers()) {
            handleMove(p);
        }
    }
}
