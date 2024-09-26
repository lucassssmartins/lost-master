package com.lostmc.login.listener;

import com.lostmc.bukkit.event.chat.PlayerChatResponseEvent;
import com.lostmc.bukkit.event.timer.ServerTimerEvent;
import com.lostmc.bukkit.event.vanish.PlayerUnvanishEvent;
import com.lostmc.bukkit.event.vanish.PlayerVanishModeEvent;
import com.lostmc.bukkit.listener.BukkitListener;
import com.lostmc.core.Commons;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.translate.Translator;
import com.lostmc.login.Login;
import com.lostmc.login.manager.LoginManager;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;

public class EventListener extends BukkitListener {

    @EventHandler
    public void onUnvanish(PlayerUnvanishEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onInitialSpawn(PlayerInitialSpawnEvent event) {
        event.setSpawnLocation(event.getPlayer().getWorld().getSpawnLocation());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage("");

        Player p = event.getPlayer();
        for (Player ps : Bukkit.getOnlinePlayers())
            if (!ps.equals(p))
                ps.hidePlayer(p);

        p.setExp(0);
        p.setHealth(p.getMaxHealth());
        p.getInventory().clear();
        p.updateInventory();

        Profile profile = Profile.getProfile(p);
        p.setPlayerListHeaderFooter(new TextComponent(Translator.tl(profile.getLocale(), "gen-hub.tablist.header")),
                new TextComponent(Translator.tl(profile.getLocale(), "gen-hub.tablist.footer")));

        Login.getControl().getController(LoginManager.class).onLoginStart(p);
    }

    @EventHandler
    public void onChat(PlayerChatResponseEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage().toLowerCase();
        if (!message.startsWith("/login") && !message.startsWith("/register")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVanishMode(PlayerVanishModeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent event) {
        event.setCancelled(event.toWeatherState());
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockIgnite(BlockIgniteEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo().getBlockY() <= 0)
            event.setTo(event.getFrom().getWorld().getSpawnLocation());
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM)
            event.setCancelled(true);
    }
}
