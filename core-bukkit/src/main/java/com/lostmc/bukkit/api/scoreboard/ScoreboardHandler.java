package com.lostmc.bukkit.api.scoreboard;

import com.lostmc.bukkit.api.scoreboard.def4lt.DefaultScoreboard;
import com.lostmc.bukkit.api.scoreboard.def4lt.DefaultScoreboardRender;
import com.lostmc.bukkit.event.timer.ServerTimerEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardHandler implements Listener {

    @Getter
    private static final ScoreboardHandler instance = new ScoreboardHandler();
    private Map<UUID, Scoreboard> scoreboards;

    public ScoreboardHandler() {
        this.scoreboards = new HashMap<>();
    }

    public void registerTimer(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void newScoreboard(Player player) {
        DefaultScoreboard scoreboard = new DefaultScoreboard();
        scoreboard.setRender(new DefaultScoreboardRender(scoreboard));
        scoreboard.createScoreboard(player);
        scoreboard.register();
        this.scoreboards.put(player.getUniqueId(), scoreboard);
    }

    public Scoreboard getScoreboard(Player player) {
        return this.scoreboards.get(player.getUniqueId());
    }

    public Scoreboard removeScoreboard(Player player) {
        return this.scoreboards.remove(player.getUniqueId());
    }

    public void update(Player player) {
        if (!this.scoreboards.containsKey(player.getUniqueId()))
            return;
        this.scoreboards.get(player.getUniqueId()).update(player);
    }

    @EventHandler
    public void timer(ServerTimerEvent event) {
        if (event.getCurrentTick() % 5 != 0)
            return;
        Bukkit.getOnlinePlayers().stream().forEach(player -> update(player));
    }
}
