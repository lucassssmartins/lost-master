package com.lostmc.bukkit.control;

import com.lostmc.bukkit.BukkitPlugin;
import org.bukkit.Server;
import org.bukkit.event.Listener;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class Controller {

	private final Control control;
	
	public BukkitPlugin getPlugin() {
		return this.control.getPlugin();
	}
	
	public Server getServer() {
		return this.control.getServer();
	}
	
	public void registerListener(Listener listener) {
		getServer().getPluginManager().registerEvents(listener, getPlugin());
	}
	
	public abstract void onEnable();
	
	public abstract void onDisable();
}
