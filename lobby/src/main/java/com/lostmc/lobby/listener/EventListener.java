package com.lostmc.lobby.listener;

import com.lostmc.bukkit.api.scoreboard.Scoreboard;
import com.lostmc.bukkit.api.scoreboard.ScoreboardHandler;
import com.lostmc.bukkit.api.title.TitleAPI;
import com.lostmc.bukkit.event.language.PlayerChangedLanguageEvent;
import com.lostmc.bukkit.listener.BukkitListener;
import com.lostmc.core.Commons;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.server.ServerType;
import com.lostmc.lobby.Lobby;
import com.lostmc.lobby.controller.LobbyController;
import com.lostmc.lobby.gamer.Gamer;
import com.lostmc.lobby.sidebar.HGLobbySidebarModel;
import com.lostmc.lobby.sidebar.MainLobbySidebarModel;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;

public class EventListener extends BukkitListener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType().isSolid()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInitialSpawn(PlayerInitialSpawnEvent event) {
        event.setSpawnLocation(Lobby.getControl().getController(LobbyController.class)
                .getSpawnLocation(event.getPlayer().getWorld()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage("");

        Player p = event.getPlayer();

        LobbyController controller = Lobby.getControl().getController(LobbyController.class);
        controller.updateHotbar(p);
        controller.updateTabList(p);
        controller.updateBossBar(p);

        Scoreboard scoreboard = ScoreboardHandler.getInstance().getScoreboard(p);
        if (Commons.getProxyHandler().getLocal().getServerType() == ServerType.HG_LOBBY) {
            scoreboard.setModel(new HGLobbySidebarModel(scoreboard));
        } else {
            scoreboard.setModel(new MainLobbySidebarModel(scoreboard));
            TitleAPI.setTitle(p, "§6§lLOST", "§eBem vindo!", 10, 10, 10);
        }

        if (p.hasPermission("hub.cmd.flight")) {
            if (!p.getAllowFlight()) {
                p.setAllowFlight(true);
                p.setFlying(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage("");
    }

    @EventHandler
    public void onLanguageChange(PlayerChangedLanguageEvent event) {
        Player p = event.getPlayer();
        LobbyController controller = Lobby.getControl().getController(LobbyController.class);
        controller.updateHotbar(p);
        controller.updateBossBar(p);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevel(FoodLevelChangeEvent event) {
        event.setFoodLevel(20);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if (p.getGameMode() != GameMode.CREATIVE ||
                !Profile.getProfile(p).getData(DataType.BUILD_MODE).getAsBoolean()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if (p.getGameMode() != GameMode.CREATIVE ||
                !Profile.getProfile(p).getData(DataType.BUILD_MODE).getAsBoolean()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onLeavesDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onAchievementAwarded(PlayerAchievementAwardedEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.DISPENSE_EGG
                && event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM
                && event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockIgnite(BlockIgniteEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        event.setCancelled(true);
    }
}
