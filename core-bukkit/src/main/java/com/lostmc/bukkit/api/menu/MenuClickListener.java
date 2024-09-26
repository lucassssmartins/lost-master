package com.lostmc.bukkit.api.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class MenuClickListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void inventory(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player p = (Player) event.getWhoClicked();
			Inventory inventory = event.getClickedInventory();
			if (inventory == null)
				return;
			if (inventory.getHolder() == null || !(inventory.getHolder() instanceof MenuHolder))
				return;
			event.setCancelled(true);
			MenuInventory menu = ((MenuHolder) inventory.getHolder()).getInventory();
			if (menu == null)
				return;
			MenuClickHandler clickHandler = menu.getClickHandler(event.getCurrentItem());
			if (clickHandler == null)
				return;
			clickHandler.onClick(p, menu, event.getClick(), event.getCurrentItem(), event.getSlot());
		}
	}
}
