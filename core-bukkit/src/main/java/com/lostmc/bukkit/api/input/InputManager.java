package com.lostmc.bukkit.api.input;

import com.lostmc.bukkit.api.input.sign.SignInputManager;
import org.bukkit.entity.Player;

public class InputManager {

	private static SignInputManager signInputManager = new SignInputManager();

	public void stop() {

	}

	public static void open(Player p, InputGui gui) {
		p.closeInventory();
		signInputManager.open(gui.getSignGui(), p, gui.getSignHandler());
	}

	public static SignInputManager getSignSearchManager() {
		return signInputManager;
	}

}
