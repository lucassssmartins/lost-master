package com.lostmc.bukkit.leaderboard;

public enum HologramTopType {

    TOP_1V1_ELO("1V1 ELO"),
    TOP_HG_ELO("HG ELO"),
    TOP_HG_KILLS("HG KILLS"),
    TOP_HG_WINS("HG WINS"),
    TOP_COINS("COINS");

    private String name;

    HologramTopType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
