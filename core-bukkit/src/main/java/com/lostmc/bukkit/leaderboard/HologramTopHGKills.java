package com.lostmc.bukkit.leaderboard;

import com.lostmc.bukkit.control.Controller;
import com.lostmc.core.profile.data.DataType;

public class HologramTopHGKills extends HologramTop {

    public HologramTopHGKills(Controller controller) {
        super(controller, DataType.HG_TOTAL_KILLS, 100, HologramTopType.TOP_HG_KILLS);
    }
}
