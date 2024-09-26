package com.lostmc.bukkit.api.input;

import com.lostmc.bukkit.api.input.sign.SignInputGui;
import com.lostmc.bukkit.api.input.sign.SignInputHandler;
import com.lostmc.bukkit.utils.string.StringLoreUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InputGui {

	private SignInputGui signGui;
	private SignInputHandler signHandler;
	private ItemStack anvilStack;

	public InputGui(String description, InputHandler handler) {
		this.signGui = new SignInputGui(description);
		this.anvilStack = new ItemStack(Material.PAPER, 1);
		ItemMeta anvilMeta = this.anvilStack.getItemMeta();
		anvilMeta.setDisplayName("");
		anvilMeta.setLore(StringLoreUtils.getLore(25, description));
		this.anvilStack.setItemMeta(anvilMeta);
		this.signHandler = new SignInputHandler() {
			@Override
			public void onDone(Player p, String[] lines) {
				handler.onDone(p, lines[0]);
			}
		};
	}

	public SignInputGui getSignGui() {
		return signGui;
	}

	public SignInputHandler getSignHandler() {
		return signHandler;
	}

	public void open(Player p) {
		InputManager.open(p, this);
	}
}
