/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package com.lostmc.bukkit.npc.internal;

public enum MinecraftVersion {

    V1_8_R3;

    public boolean isAboveOrEqual(MinecraftVersion compare) {
        return ordinal() >= compare.ordinal();
    }
}
