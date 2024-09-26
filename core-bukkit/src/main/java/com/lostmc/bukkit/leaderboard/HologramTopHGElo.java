package com.lostmc.bukkit.leaderboard;

import com.lostmc.bukkit.control.Controller;
import com.lostmc.core.profile.data.DataType;

public class HologramTopHGElo extends HologramTop {

    public HologramTopHGElo(Controller controller) {
        super(controller, DataType.HG_ELO, 100, HologramTopType.TOP_HG_ELO);
    }
}
