package com.lostmc.hungergames.manager;

import com.lostmc.bukkit.event.timer.ServerTimerEvent;
import com.lostmc.game.GamePlugin;
import com.lostmc.game.manager.Management;
import com.lostmc.hungergames.status.StatusHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class AssistManager extends Management implements Listener {

    private Map<UUID, Map<UUID, Long>> assists = new HashMap<>();

    public AssistManager(GamePlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        registerListener(this);
    }

    public void addAssist(Player target, Player player) {
        getAssistsMap(target).put(player.getUniqueId(), System.currentTimeMillis() + 5000L);
    }

    public Map<UUID, Long> getAssistsMap(Player p) {
        return assists.computeIfAbsent(p.getUniqueId(), v -> new HashMap<>());
    }

    public void checkForAssistences(Player p) {
       getAssistences(p).forEach(ps -> StatusHandler.addAssist(ps));
    }

    public List<Player> getAssistences(Player death) {
        List<Player> assistences = new ArrayList<>();
        for (UUID uuid : getAssistsMap(death).keySet()) {
            Player next = Bukkit.getPlayer(uuid);
            if (next != null) {
                assistences.add(next);
            }
        }
        return assistences;
    }

    @EventHandler
    public void onTimer(ServerTimerEvent event) {
        if (event.getCurrentTick() % 20 != 0)
            return;
        for (Player ps : getServer().getOnlinePlayers()) {
            if (!ps.isOnline())
                continue;
            Map<UUID, Long> map = getAssistsMap(ps);
            Iterator<Map.Entry<UUID, Long>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<UUID, Long> next = it.next();
                if (next.getValue() < System.currentTimeMillis()) {
                    it.remove();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        assists.remove(event.getPlayer());
    }

    @Override
    public void onDisable() {

    }
}
