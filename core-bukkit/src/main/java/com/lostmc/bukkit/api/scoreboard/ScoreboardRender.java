package com.lostmc.bukkit.api.scoreboard;

import lombok.Getter;

@Getter
public abstract class ScoreboardRender {

    private Scoreboard scoreboard;

    public ScoreboardRender(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public abstract void renderScore(int id, String prefix, String suffix);
}
