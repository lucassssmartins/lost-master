package com.lostmc.bukkit.leaderboard;

import com.lostmc.bukkit.control.Controller;
import com.lostmc.core.profile.data.DataType;

public class NpcTopHGWins extends NpcTop {

    public NpcTopHGWins(Controller controller) {
        super(controller, DataType.HG_TOTAL_WINS, 3, NpcTopType.HG_WINS);
    }
}
