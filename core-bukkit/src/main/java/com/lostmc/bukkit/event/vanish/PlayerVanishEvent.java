package com.lostmc.bukkit.event.vanish;

import org.bukkit.entity.Player;

public class PlayerVanishEvent extends VanishEvent {

    public PlayerVanishEvent(Player player, Player viewer) {
        super(player, viewer);
    }
}
