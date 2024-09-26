package com.lostmc.bukkit.api.input.sign;

import org.bukkit.entity.Player;

public interface SignGUIListener {

    void onSignDone(Player player, String[] lines);
}
