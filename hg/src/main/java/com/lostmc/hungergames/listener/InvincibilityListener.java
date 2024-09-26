package com.lostmc.hungergames.listener;

import com.lostmc.hungergames.constructor.HungerListener;
import com.lostmc.hungergames.event.game.GameStageChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class InvincibilityListener extends HungerListener {

	private boolean isInvincibility() {
		return getMain().getGameStage().isInvincibility();
	}

	@EventHandler
	public void onRegen(EntityRegainHealthEvent event) {
		if (isInvincibility())
			event.setCancelled(true);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (isInvincibility())
			event.setFoodLevel(20);
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		if (isInvincibility())
			event.setCancelled(true);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		if (isInvincibility())
			event.setCancelled(true);
	}

	@EventHandler
	public void onGameStageChange(GameStageChangeEvent event) {
		if (event.getLastStage().isInvincibility()) {
			if (!event.getNewStage().isInvincibility()) {
				HandlerList.unregisterAll(this);
			}
		}
	}
}