package com.lostmc.bukkit.event.timer;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServerTimerEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	@Getter
	private final long currentTick;

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
