package com.lostmc.bukkit.api.input;

import org.bukkit.entity.Player;

public interface InputHandler {

	public abstract void onDone(Player p, String name);

	public abstract void onClose(Player p);

}
