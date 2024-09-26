package com.lostmc.bukkit.leaderboard;

import java.util.Locale;

public enum NpcTopType {

    PVP_1V1_ELO,
    HG_ELO,
    HG_WINS,
    HG_KILLS;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
