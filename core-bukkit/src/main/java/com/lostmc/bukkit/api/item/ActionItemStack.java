package com.lostmc.bukkit.api.item;

import java.util.HashMap;

import com.lostmc.bukkit.utils.nbt.SafeNBT;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ActionItemStack implements Listener {

	private static final HashMap<Integer, InteractHandler> handlers = new HashMap<>();

	private static int HANDLER_ID = 0;

	public static int register(InteractHandler handler) {
		if (handlers.containsKey(HANDLER_ID))
			return -1;
		handlers.put(HANDLER_ID, handler);
		++HANDLER_ID;
		return HANDLER_ID - 1;
	}

	public static void unregister(Integer id) {
		handlers.remove(id);
	}

	public static InteractHandler getHandler(Integer id) {
		return handlers.get(id);
	}

	public static ItemStack setTag(ItemStack stack, int id) {
		try {
			if (stack == null || stack.getType() == Material.AIR)
				throw new Exception();
			SafeNBT compound = SafeNBT.get(stack);
			compound.setInt("interactHandler", id);
			return compound.apply(stack);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@EventHandler
	public void interact(PlayerInteractEvent event) {
		if (event.getItem() == null)
			return;
		ItemStack stack = event.getItem();
		try {
			if (stack == null || stack.getType() == Material.AIR)
				throw new Exception();
			SafeNBT compound = SafeNBT.get(stack);
			if (!compound.hasKey("interactHandler")) {
				return;
			}
			InteractHandler handler = getHandler(compound.getInt("interactHandler"));
			if (handler == null) {
				throw new NullPointerException();
			}
			Player player = event.getPlayer();
			Action action = event.getAction();
			event.setCancelled(handler.onInteract(player, stack, action, event.getClickedBlock()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
