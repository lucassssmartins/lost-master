package com.lostmc.bukkit.api.cooldown.event;

import com.lostmc.bukkit.api.cooldown.types.Cooldown;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class CooldownDisplayEvent extends CooldownEvent implements Cancellable {
	
	private boolean cancelled = false;

	public CooldownDisplayEvent(Player player, Cooldown cooldown) {
		super(player, cooldown);
	}

	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
}
