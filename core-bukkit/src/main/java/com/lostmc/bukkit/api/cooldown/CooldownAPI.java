package com.lostmc.bukkit.api.cooldown;

import com.lostmc.bukkit.api.actionbar.ActionBarAPI;
import com.lostmc.bukkit.api.cooldown.event.CooldownDisplayEvent;
import com.lostmc.bukkit.api.cooldown.event.CooldownFinishEvent;
import com.lostmc.bukkit.api.cooldown.event.CooldownStartEvent;
import com.lostmc.bukkit.api.cooldown.types.Cooldown;
import com.lostmc.bukkit.api.cooldown.types.ItemCooldown;
import com.lostmc.bukkit.event.timer.ServerTimerEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownAPI implements Listener {

	private static final char CHAR = '|';
	private static final Map<UUID, List<Cooldown>> map = new ConcurrentHashMap<>();

	public static void addCooldown(Player player, Cooldown cooldown) {
		CooldownStartEvent event = new CooldownStartEvent(player, cooldown);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			List<Cooldown> list = map.computeIfAbsent(player.getUniqueId(), v -> new ArrayList<>());
			list.add(cooldown);
		}
	}

	public static boolean removeCooldown(Player player, String name) {
		List<Cooldown> list = map.get(player.getUniqueId());
		if (list != null) {
			Iterator<Cooldown> it = list.iterator();
			while (it.hasNext()) {
				Cooldown cooldown = it.next();
				if (cooldown.getName().equals(name)) {
					it.remove();
					return true;
				}
			}
		}
		return false;
	}

	public static boolean hasCooldown(Player player, String name) {
		List<Cooldown> list = map.get(player.getUniqueId());
		if (list != null) {
			for (Cooldown cooldown : list) {
				if (!cooldown.getName().equals(name))
					continue;
				return true;
			}
		}
		return false;
	}

	public static Cooldown getCooldown(Player player, String name) {
		List<Cooldown> list = map.get(player.getUniqueId());
		if (list != null) {
			for (Cooldown cooldown : list) {
				if (!cooldown.getName().equals(name))
					continue;
				return cooldown;
			}
		}
		return null;
	}

	@EventHandler
	public void quit(PlayerQuitEvent event) {
		map.remove(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void timer(ServerTimerEvent event) {
		if (event.getCurrentTick() % 2 > 0)
			return;

		for (UUID uuid : map.keySet()) {
			Player player = Bukkit.getPlayer(uuid);
			if (player != null) {
				List<Cooldown> list = map.get(uuid);
				Iterator<Cooldown> it = list.iterator();

				Cooldown found = null;
				while (it.hasNext()) {
					Cooldown cooldown = it.next();
					if (!cooldown.expired()) {
						if (cooldown instanceof ItemCooldown) {
							ItemStack hand = player.getEquipment().getItemInHand();
							if (hand != null && hand.getType() != Material.AIR) {
								ItemCooldown item = (ItemCooldown) cooldown;
								if (hand.equals(item.getItem())) {
									item.setSelected(true);
									found = item;
									break;
								}
							}
							continue;
						}
						found = cooldown;
						continue;
					}
					it.remove();
					Bukkit.getServer().getPluginManager().callEvent(new CooldownFinishEvent(player, cooldown));
				}

				if (found != null) {
					CooldownDisplayEvent e = new CooldownDisplayEvent(player, found);
					Bukkit.getPluginManager().callEvent(e);
					if (!e.isCancelled()) {
						display(player, found);
					}
				} else if (list.isEmpty()) {
					ActionBarAPI.send(player, " ");
					map.remove(uuid);
				} else {
					Cooldown cooldown = list.get(0);
					if (cooldown instanceof ItemCooldown) {
						ItemCooldown item = (ItemCooldown) cooldown;
						if (item.isSelected()) {
							item.setSelected(false);
							ActionBarAPI.send(player, " ");
						}
					}
				}
			}
		}
	}

	private void display(Player player, Cooldown cooldown) {
		StringBuilder bar = new StringBuilder();
		double percentage = cooldown.getPercentage();
		double remaining = cooldown.getRemaining();
		double count = 20 - Math.max(percentage > 0D ? 1 : 0, percentage / 5);
		for (int a = 0; a < count; a++)
			bar.append("§a" + CHAR);
		for (int a = 0; a < 20 - count; a++)
			bar.append("§c" + CHAR);
		String name = cooldown.getName();
		ActionBarAPI.send(player, name + " " + bar.toString() + "§f " + String.format("%.1fs", remaining));
	}

	public static String getCooldownFormat(Player player, String name) {
		Cooldown cooldown = getCooldown(player, name);
		if (cooldown == null || cooldown.getRemaining() < 0.0)
			return "0,0s";
		return String.format("%.1fs", cooldown.getRemaining());
	}
}
