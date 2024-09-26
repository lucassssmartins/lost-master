package com.lostmc.bukkit.permission;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

public class PermissionListener implements Listener {

	private BukkitPermissionManager manager;

	public PermissionListener(BukkitPermissionManager manager) {
		this.manager = manager;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLogin(PlayerLoginEvent event) {
		final Player player = event.getPlayer();
		this.manager.getRegex().injectPermissible(player);
		this.manager.updatePermissions(player);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onMonitorLogin(PlayerLoginEvent event) {
		if (event.getResult() != Result.ALLOWED) {
			this.manager.clearPermissions(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		this.manager.clearPermissions(event.getPlayer());
		this.manager.getRegex().uninjectPermissible(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onKick(PlayerKickEvent event) {
		this.manager.clearPermissions(event.getPlayer());
		this.manager.getRegex().uninjectPermissible(event.getPlayer());
	}
}
