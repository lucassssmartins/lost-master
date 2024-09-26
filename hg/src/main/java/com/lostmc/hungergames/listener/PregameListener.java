package com.lostmc.hungergames.listener;

import com.lostmc.bukkit.api.title.TitleAPI;
import com.lostmc.bukkit.event.vanish.PlayerVanishModeEvent;
import com.lostmc.bukkit.vanish.VanishController;
import com.lostmc.core.Commons;
import com.lostmc.game.manager.Management;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.constructor.HungerGamer;
import com.lostmc.hungergames.constructor.HungerListener;
import com.lostmc.hungergames.event.game.GameStageChangeEvent;
import com.lostmc.hungergames.manager.GamerManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class PregameListener extends HungerListener {

	private boolean isPregame() {
		return getMain().getGameStage().isPregame();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {
		if (!isPregame())
			return;
		event.setJoinMessage(null);
		Player p = event.getPlayer();
		HungerGamer gamer = HungerGamer.getGamer(p);
		p.setHealth(20.0);
		p.setFoodLevel(20);
		p.setExp(0);
		if (HungerGames.getControl().getController(VanishController.class).isVanished(p)) {
			gamer.setGamerState(HungerGamer.GamerState.SPECTATOR);
		} else {
			gamer.setGamerState(HungerGamer.GamerState.ALIVE);
			p.setGameMode(GameMode.SURVIVAL);
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (isPregame())
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if (isPregame())
			event.setCancelled(true);
	}

	@EventHandler
	public void onRegen(EntityRegainHealthEvent event) {
		if (isPregame())
			event.setCancelled(true);
	}

	@EventHandler
	public void onExpChange(PlayerExpChangeEvent event) {
		if (isPregame())
			event.setAmount(0);
	}

	@EventHandler
	public void onMobTarget(EntityTargetEvent event) {
		if (isPregame())
			event.setCancelled(true);
	}

	@EventHandler
	public void onDropItem(PlayerDropItemEvent event) {
		if (isPregame())
			event.setCancelled(true);
	}

	@EventHandler
	public void onPickupItem(PlayerPickupItemEvent event) {
		if (isPregame())
			event.setCancelled(true);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (isPregame())
			event.setFoodLevel(20);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (isPregame())
			event.setCancelled(true);
	}

	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent event) {
		if (isPregame())
			event.setCancelled(true);
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (isPregame())
			event.setCancelled(true);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (isPregame())
			event.setCancelled(true);
	}

	@EventHandler
	public void onShear(PlayerShearEntityEvent event) {
		if (isPregame())
			event.setCancelled(true);
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();
		p.setHealth(p.getMaxHealth());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityExplode(EntityExplodeEvent event) {
		if (isPregame())
			event.setCancelled(true);
	}

	@EventHandler
	public void onPortal(PlayerPortalEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (event.getCause() == TeleportCause.NETHER_PORTAL || event.getCause() == TeleportCause.END_PORTAL)
			event.setCancelled(true);
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() == SpawnReason.CUSTOM)
			return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		Management.getManagement(GamerManager.class).removeGamer(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onGameStageChange(GameStageChangeEvent event) {
		if (event.getLastStage().isPregame()) {
			if (!event.getNewStage().isPregame()) {
				HandlerList.unregisterAll(this);
			}
		}
	}
}
