package com.lostmc.bukkit.api.menu;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryCustom;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.inventivetalent.reflection.resolver.FieldResolver;
import org.inventivetalent.reflection.util.AccessUtil;

public class MenuInventory extends CraftInventoryCustom {

	private final Map<ItemStack, MenuClickHandler> handlers = new HashMap<>();

	public MenuInventory(int size, String title) {
		super(null, size, title.length() > 32 ? title.substring(0, 32) : title);
		injectHolder();
	}

	public MenuInventory(InventoryType type, String title) {
		super(null, type, title.length() > 32 ? title.substring(0, 32) : title);
		injectHolder();
	}

	public MenuInventory(InventoryType type) {
		super(null, type);
		injectHolder();
	}

	public void setItem(int index, ItemStack item, MenuClickHandler click) {
		super.setItem(index, item);
		this.handlers.put(item, click);
	}

	public HashMap<Integer, ItemStack> addItem(MenuClickHandler handler, ItemStack... items) {
		HashMap<Integer, ItemStack> map = super.addItem(items);
		for (ItemStack item : items)
			this.handlers.put(item, handler);
		return map;
	}

	public MenuClickHandler getClickHandler(ItemStack item) {
		if (item == null)
			return null;
		return this.handlers.get(item);
	}

	private void injectHolder() {
		try {
			AccessUtil.setAccessible(new FieldResolver(
					Class.forName("org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryCustom$MinecraftInventory"))
							.resolve("owner"))
					.set(getInventory(), new MenuHolder(this));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
