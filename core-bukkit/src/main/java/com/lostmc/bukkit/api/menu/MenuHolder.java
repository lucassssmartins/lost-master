package com.lostmc.bukkit.api.menu;

import org.bukkit.inventory.InventoryHolder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MenuHolder implements InventoryHolder {
	
	private final MenuInventory inventory;

	@Override
	public MenuInventory getInventory() {
		return this.inventory;
	}
}
