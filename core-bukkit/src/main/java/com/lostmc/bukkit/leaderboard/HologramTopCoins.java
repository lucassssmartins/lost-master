package com.lostmc.bukkit.leaderboard;

import com.lostmc.bukkit.control.Controller;
import com.lostmc.core.profile.data.DataType;

public class HologramTopCoins extends HologramTop {

    public HologramTopCoins(Controller controller) {
        super(controller, DataType.COINS, 100, HologramTopType.TOP_COINS);
    }
}
