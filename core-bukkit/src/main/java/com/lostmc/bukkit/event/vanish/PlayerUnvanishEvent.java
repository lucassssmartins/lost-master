package com.lostmc.bukkit.event.vanish;

import org.bukkit.entity.Player;

public class PlayerUnvanishEvent extends VanishEvent {

    public PlayerUnvanishEvent(Player player, Player viewer) {
        super(player, viewer);
    }
}
