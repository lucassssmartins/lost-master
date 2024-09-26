package com.lostmc.bukkit.leaderboard;

import com.lostmc.bukkit.control.Control;
import com.lostmc.bukkit.control.Controller;
import com.lostmc.bukkit.event.timer.ServerTimerEvent;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class HologramTopController extends Controller implements Listener {

    private final Map<HologramTopType, HologramTop> topHolograms = new HashMap<>();

    public HologramTopController(Control control) {
        super(control);
    }

    @Override
    public void onEnable() {
        for (HologramTopType topType : HologramTopType.values()) {
            if (topType == HologramTopType.TOP_COINS) {
                topHolograms.put(topType, new HologramTopCoins(this));
            } else if (topType == HologramTopType.TOP_HG_WINS) {
                topHolograms.put(topType, new HologramTopHGWins(this));
            } else if (topType == HologramTopType.TOP_1V1_ELO) {
                topHolograms.put(topType, new HologramTop1V1Elo(this));
            } else if (topType == HologramTopType.TOP_HG_KILLS) {
                topHolograms.put(topType, new HologramTopHGKills(this));
            } else if (topType == HologramTopType.TOP_HG_ELO) {
                topHolograms.put(topType, new HologramTopHGElo(this));
            }
        }
        getServer().getScheduler().runTaskAsynchronously(getPlugin(), () -> topHolograms.values()
                .forEach(e -> e.start()));
        registerListener(this);
    }

    @EventHandler
    public void onServerTimer(ServerTimerEvent event) {
        if (event.getCurrentTick() % (1200 * 5) != 0)
            return;
        getServer().getScheduler().runTaskAsynchronously(getPlugin(), () -> topHolograms.values()
                .forEach(e -> e.onUpdate()));
    }

    public void saveLocation(HologramTopType topType, Location location) {
        HologramTop top = topHolograms.get(topType);
        if (top != null) {
            top.saveLocation(location);
        }
    }

    @Override
    public void onDisable() {

    }
}
