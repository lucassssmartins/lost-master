package com.lostmc.bukkit.api.scoreboard;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
public abstract class ScoreboardModel {

    private final Scoreboard scoreboard;

    public ScoreboardModel(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public abstract List<String> getModel(Player player);
}
