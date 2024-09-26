package com.lostmc.bukkit.control;

import java.util.HashMap;
import java.util.Map;

import com.lostmc.bukkit.chat.ChatController;
import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.bukkit.hologram.HologramManager;
import com.lostmc.bukkit.nametag.NametagController;
import com.lostmc.bukkit.npc.NpcController;
import com.lostmc.bukkit.vanish.VanishController;
import org.bukkit.Server;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Control {

	private final BukkitPlugin plugin;
	private Map<Class<?>, Controller> controllers = new HashMap<>();
	
	public Server getServer() {
		return getPlugin().getServer();
	}
	
	public void onEnable() {
		enableController(ChatController.class);
		enableController(NametagController.class);
		enableController(HologramManager.class);
		enableController(NpcController.class);
		enableController(VanishController.class);
	}
	
	public <T> T getController(Class<T> cls) {
		return cls.cast(this.controllers.get(cls));
	}
	
	public void enableController(Class<? extends Controller> cls) {
		if (this.controllers.containsKey(cls))
			throw new IllegalStateException("Controller '" + cls.getName() + "' already enabled");
		
		try {
			Controller controllers = cls.getConstructor(Control.class).newInstance(this);
			controllers.onEnable();
			this.controllers.put(cls, controllers);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.plugin.getLogger().info("Controller '" + cls.getName() + "' enabled");
		}
	}
	
	public void disableController(Class<? extends Controller> cls) {
		if (!this.controllers.containsKey(cls))
			return;
		
		Controller controllers = this.controllers.get(cls);
		try {
			controllers.onDisable();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.plugin.getLogger().info("Controller '" + cls.getName() + "' correctly disabled");
		}
		
		this.controllers.remove(cls);
	}
	
	public void destroy() {
		this.controllers.values().forEach(controllers -> {
			try {
				controllers.onDisable();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				this.plugin.getLogger().info("Controller '" + controllers.getClass().getName() + "' correctly disabled");
			}
		});
		
		this.controllers.clear();
		this.controllers = null;
	}
}
