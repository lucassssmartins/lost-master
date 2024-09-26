package com.lostmc.hungergames.util;

import com.lostmc.game.constructor.Kit;
import com.lostmc.hungergames.constructor.HungerGamer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Iterator;
import java.util.List;

public class ItemUtils {

	public static void dropAndClear(Player p, List<ItemStack> items, Location l) {
		Iterator<ItemStack> iterator = items.iterator();
		while (iterator.hasNext()) {
			ItemStack item = iterator.next();
			for (Kit kit : HungerGamer.getGamer(p).getKits().values()) {
				if (kit.isKitItem(item)) {
					iterator.remove();
					break;
				}
			}
		}
		dropItems(items, l);
		p.closeInventory();
		p.getInventory().setArmorContents(new ItemStack[4]);
		p.getInventory().clear();
		p.setItemOnCursor(null);
		for (PotionEffect pot : p.getActivePotionEffects()) {
			p.removePotionEffect(pot.getType());
			break;
		}
	}

	public static void dropItems(List<ItemStack> items, Location l) {
		World world = l.getWorld();
		for (ItemStack item : items) {
			if (item == null || item.getType() == Material.AIR)
				continue;
			if (item.hasItemMeta())
				world.dropItemNaturally(l, item.clone()).getItemStack().setItemMeta(item.getItemMeta());
			else
				world.dropItemNaturally(l, item);
		}
	}

	public static boolean isEquals(ItemStack item, ItemStack it) {
		if (it.getType() == item.getType() && it.getDurability() == item.getDurability()) {
			if (it.hasItemMeta() && item.hasItemMeta()) {
				if (it.getItemMeta().hasDisplayName() && item.getItemMeta().hasDisplayName()) {
					if (item.getItemMeta().getDisplayName().equals(it.getItemMeta().getDisplayName()))
						return true;
				} else if (!it.getItemMeta().hasDisplayName() && !item.getItemMeta().hasDisplayName())
					return true;
			} else if (!it.hasItemMeta() && !item.hasItemMeta())
				return true;
		}
		return false;
	}
}
