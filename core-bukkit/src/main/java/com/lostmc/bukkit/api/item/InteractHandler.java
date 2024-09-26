package com.lostmc.bukkit.api.item;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public interface InteractHandler {

	boolean onInteract(Player player, ItemStack item, Action action, Block clicked);
}
