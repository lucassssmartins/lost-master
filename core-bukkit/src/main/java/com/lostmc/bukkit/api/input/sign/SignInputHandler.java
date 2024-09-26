package com.lostmc.bukkit.api.input.sign;

import org.bukkit.entity.Player;

public interface SignInputHandler {

	public void onDone(Player p, String[] lines);

}
