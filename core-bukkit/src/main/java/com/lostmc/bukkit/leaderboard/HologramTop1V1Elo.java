package com.lostmc.bukkit.leaderboard;

import com.lostmc.bukkit.control.Controller;
import com.lostmc.core.profile.data.DataType;

public class HologramTop1V1Elo extends HologramTop {

    public HologramTop1V1Elo(Controller controller) {
        super(controller, DataType.FIGHT_1V1_ELO, 100, HologramTopType.TOP_1V1_ELO);
    }
}
