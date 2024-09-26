package com.lostmc.bukkit.leaderboard;

import com.lostmc.bukkit.control.Controller;
import com.lostmc.core.profile.data.DataType;

public class HologramTopHGWins extends HologramTop {

    public HologramTopHGWins(Controller controller) {
        super(controller, DataType.HG_TOTAL_WINS, 100, HologramTopType.TOP_HG_WINS);
    }
}
