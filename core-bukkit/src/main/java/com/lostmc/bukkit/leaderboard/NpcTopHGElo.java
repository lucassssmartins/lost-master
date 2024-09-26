package com.lostmc.bukkit.leaderboard;

import com.lostmc.bukkit.control.Controller;
import com.lostmc.core.profile.data.DataType;

public class NpcTopHGElo extends NpcTop {

    public NpcTopHGElo(Controller controller) {
        super(controller, DataType.HG_ELO, 3, NpcTopType.HG_ELO);
    }
}
