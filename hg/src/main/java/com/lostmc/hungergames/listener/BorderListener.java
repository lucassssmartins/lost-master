package com.lostmc.hungergames.listener;

import com.lostmc.bukkit.event.timer.ServerTimerEvent;
import com.lostmc.bukkit.vanish.VanishController;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.constructor.HungerGamer;
import com.lostmc.hungergames.constructor.HungerListener;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.Map;

public class BorderListener extends HungerListener {

    public Map<Player, Location> locations = new HashMap<>();

    @EventHandler
    public void onTimer(ServerTimerEvent event) {
        if (event.getCurrentTick() % 20 != 0)
            return;
        for (Player p : Bukkit.getOnlinePlayers()) {
            HungerGamer gamer = HungerGamer.getGamer(p);
            if (gamer.isNotPlaying() || p.getGameMode() == GameMode.CREATIVE
                    || p.getGameMode() == GameMode.SPECTATOR ||
                    HungerGames.getControl().getController(VanishController.class)
                            .isVanished(p))
                continue;
            if (isOnWarning(p)) {
                if (!getMain().getGameStage().isPregame()) {
                    p.sendMessage("§cVocê está próximo da borda!");
                    continue;
                }
            }
            if (isNotInBoard(p) || p.getLocation().getY() > 129) {
                if (getMain().getGameStage().isPregame()) {
                    if (locations.containsKey(p)) {
                        p.teleport(locations.get(p));
                        continue;
                    }
                } else {
                    p.sendMessage("§cVocê está na borda!");
                    EntityDamageEvent e = new EntityDamageEvent(p, EntityDamageEvent.DamageCause.CUSTOM, 4.0d);
                    if (e.isCancelled())
                        e.setCancelled(false);
                    p.setLastDamageCause(e);
                    p.damage(4.0);
                }
            }
            locations.put(p, p.getLocation());
        }
    }

    private boolean isNotInBoard(Player p) {
        int size = (int) 1000 / 2;
        return ((p.getLocation().getBlockX() > size) || (p.getLocation().getBlockX() < -size)
                || (p.getLocation().getBlockZ() > size) || (p.getLocation().getBlockZ() < -size));
    }

    private boolean isOnWarning(Player p) {
        int size = (int) 1000 / 2;
        size = size - 20;
        return !isNotInBoard(p) && ((p.getLocation().getBlockX() > size) || (p.getLocation().getBlockX() < -size)
                || (p.getLocation().getBlockZ() > size) || (p.getLocation().getBlockZ() < -size));
    }
}
