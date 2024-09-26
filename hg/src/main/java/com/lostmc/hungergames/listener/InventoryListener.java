package com.lostmc.hungergames.listener;

import com.lostmc.bukkit.api.item.InteractHandler;
import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.core.profile.Profile;
import com.lostmc.hungergames.constructor.HungerListener;
import com.lostmc.hungergames.event.game.GameStartEvent;
import com.lostmc.hungergames.menu.KitMenu;
import com.lostmc.hungergames.menu.StoreMenu;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryListener extends HungerListener {

	public static ItemStack singleChest;
	public static ItemStack doubleChest;
	public static ItemStack kitStore;

	public InventoryListener() {
		singleChest = new ItemBuilder(Material.CHEST)
				.setName("§aSelecionar kit").build(new InteractHandler() {
					@Override
					public boolean onInteract(Player player, ItemStack item, Action action, Block clicked) {
						player.openInventory(new KitMenu(player, false));
						return true;
					}
				});
		doubleChest = new ItemBuilder(Material.CHEST)
				.setName("§aSelecionar kit 2").build(new InteractHandler() {
					@Override
					public boolean onInteract(Player player, ItemStack item, Action action, Block clicked) {
						if (getMain().getGameMode().isDoubleKit()) {
							player.openInventory(new KitMenu(player, true));
						} else {
							player.sendMessage("§cEste torneio já não é mais double kit.");
						}
						return true;
					}
				});
		kitStore = new ItemBuilder(Material.EMERALD)
				.setName("§aLoja de kits §7(Clique)").build(new InteractHandler() {
					@Override
					public boolean onInteract(Player player, ItemStack item, Action action, Block clicked) {
						player.openInventory(new StoreMenu(player, Profile.getProfile(player), 1));
						return true;
					}
				});
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.getPlayer().getInventory().clear();

		event.getPlayer().getInventory().setItem(0, singleChest);
		if (getMain().getGameMode().isDoubleKit())
			event.getPlayer().getInventory().setItem(1, doubleChest);
		event.getPlayer().getInventory().setItem(8, kitStore);
		event.getPlayer().updateInventory();
	}

	private boolean isEqual(ItemStack item, ItemStack item2) {
		if (item.getType() != item2.getType())
			return false;
		if (!item.hasItemMeta()) {
			if (item2.hasItemMeta())
				return false;
			return true;
		}
		if (!item.getItemMeta().hasDisplayName()) {
			if (item2.getItemMeta().hasDisplayName())
				return false;
			return true;
		}
		if (!item.getItemMeta().getDisplayName().equals(item2.getItemMeta().getDisplayName()))
			return false;
		return true;
	}

	@EventHandler
	public void onGameStart(GameStartEvent event) {
		HandlerList.unregisterAll(this);
	}
}
