package com.lostmc.bukkit.leaderboard;

import com.lostmc.bukkit.control.Controller;
import com.lostmc.core.profile.data.DataType;

public class NpcTop1V1Elo extends NpcTop {

    public NpcTop1V1Elo(Controller controller) {
        super(controller, DataType.FIGHT_1V1_ELO, 3, NpcTopType.PVP_1V1_ELO);
    }
}
