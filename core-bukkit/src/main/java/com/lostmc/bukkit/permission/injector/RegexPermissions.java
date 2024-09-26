package com.lostmc.bukkit.permission.injector;

import java.util.logging.Level;

import com.lostmc.bukkit.permission.BukkitPermissionManager;
import com.lostmc.bukkit.permission.PermissibleCORE;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

public class RegexPermissions {

	private final BukkitPermissionManager plugin;
	private PermissionList permsList;
	private PermissionSubscriptionMap subscriptionHandler;
	
	private final String CRAFT_HUMAN_ENTITY_CLASS_NAME = CraftHumanEntity.class.getCanonicalName();

	public RegexPermissions(BukkitPermissionManager plugin) {
		this.plugin = plugin;
		subscriptionHandler = PermissionSubscriptionMap.inject(plugin.getPlugin(),
				plugin.getServer().getPluginManager());
		permsList = PermissionList.inject(plugin.getServer().getPluginManager());
		injectAllPermissibles();
	}

	public void onDisable() {
		subscriptionHandler.uninject();
		uninjectAllPermissibles();
	}

	public PermissionList getPermissionList() {
		return permsList;
	}

	public void injectPermissible(Player player) {
		try {
			PermissibleCORE permissible = new PermissibleCORE(player, plugin);
			PermissibleInjector injector = new PermissibleInjector.ClassPresencePermissibleInjector(
					CRAFT_HUMAN_ENTITY_CLASS_NAME, "perm", true);
			boolean success = false;
			if (injector.isApplicable(player)) {
				Permissible oldPerm = injector.inject(player, permissible);
				if (oldPerm != null) {
					permissible.setPreviousPermissible(oldPerm);
					success = true;
				}
			}

			if (!success) {
				plugin.getPlugin().getLogger().warning("Unable to inject Core's permissible for " + player.getName());
			}

			permissible.recalculatePermissions();

		} catch (Throwable e) {
			plugin.getPlugin().getLogger().log(Level.SEVERE, "Unable to inject permissible for " + player.getName(), e);
		}
	}

	public void injectAllPermissibles() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			injectPermissible(player);
		}
	}

	public void uninjectPermissible(Player player) {
		try {
			boolean success = false;
			PermissibleInjector injector = new PermissibleInjector.ClassPresencePermissibleInjector(
					CRAFT_HUMAN_ENTITY_CLASS_NAME, "perm", true);
			if (injector.isApplicable(player)) {
				Permissible pexPerm = injector.getPermissible(player);
				if (pexPerm instanceof PermissibleCORE) {
					if (injector.inject(player, ((PermissibleCORE) pexPerm).getPreviousPermissible()) != null) {
						success = true;
					}
				} else {
					success = true;
				}
			}
			if (!success) {
				plugin.getPlugin().getLogger()
						.warning("No Permissible injector found for your server implementation (while uninjecting for "
								+ player.getName() + "!");
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void uninjectAllPermissibles() {
		for (Player player : plugin.getServer().getOnlinePlayers()) {
			uninjectPermissible(player);
		}
	}
}