package com.lostmc.bukkit.leaderboard;

import com.lostmc.bukkit.control.Controller;
import com.lostmc.core.profile.data.DataType;

public class NpcTopHGKills extends NpcTop {

    public NpcTopHGKills(Controller controller) {
        super(controller, DataType.HG_TOTAL_KILLS, 3, NpcTopType.HG_KILLS);
    }
}
