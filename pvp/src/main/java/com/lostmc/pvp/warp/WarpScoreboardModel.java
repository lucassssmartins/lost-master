package com.lostmc.pvp.warp;

import com.google.common.collect.Lists;
import com.lostmc.bukkit.api.scoreboard.Scoreboard;
import com.lostmc.bukkit.api.scoreboard.ScoreboardModel;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class WarpScoreboardModel extends ScoreboardModel {

    protected final List<String> perPlayer = Lists.newLinkedList();

    public WarpScoreboardModel(Scoreboard scoreboard) {
        super(scoreboard);
    }
}
