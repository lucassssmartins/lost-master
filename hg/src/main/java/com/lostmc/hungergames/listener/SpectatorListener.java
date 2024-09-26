package com.lostmc.hungergames.listener;

import com.lostmc.bukkit.event.chat.PlayerChatResponseEvent;
import com.lostmc.bukkit.vanish.VanishController;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.constructor.HungerGamer;
import com.lostmc.hungergames.constructor.HungerListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;

public class SpectatorListener extends HungerListener {

    private boolean isNotPlaying(Player player) {
        HungerGamer gamer = HungerGamer.getGamer(player);
        return gamer.isNotPlaying();
    }

    public boolean isAdmin(Player player) {
        HungerGamer gamer = HungerGamer.getGamer(player);
        return gamer.isGamemaker() || HungerGames.getControl().getController(VanishController.class)
                .isVanished(player);
    }

    @EventHandler
    public void onChat(PlayerChatResponseEvent event) {
        if (isNotPlaying(event.getPlayer()) && !isAdmin(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cO bate-papo global é apenas para participantes do torneio.");
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (isNotPlaying(event.getPlayer()) && !isAdmin(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (isNotPlaying(event.getPlayer()) && !isAdmin(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onRegen(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player)
            if (isNotPlaying((Player) event.getEntity()))
                event.setCancelled(true);
    }

    @EventHandler
    public void onExpChange(PlayerExpChangeEvent event) {
        if (isNotPlaying(event.getPlayer()))
            event.setAmount(0);
    }

    @EventHandler
    public void onMobTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player)
            if (isNotPlaying((Player) event.getTarget()))
                event.setCancelled(true);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if (isNotPlaying(event.getPlayer()) && !isAdmin(event.getPlayer())) {
            event.setCancelled(true);
            return;
        }
        if (HungerGamer.getGamer(event.getPlayer()).hasPrimaryKit()) {
            if (HungerGamer.getGamer(event.getPlayer()).getPrimaryKit().isKitItem(event.getPlayer().getItemInHand())) {
                event.setCancelled(true);
                return;
            }
        }
        if (HungerGamer.getGamer(event.getPlayer()).hasSecondaryKit()) {
            if (HungerGamer.getGamer(event.getPlayer()).getSecondaryKit().isKitItem(event.getPlayer().getItemInHand())) {
                event.setCancelled(true);
                return;
            }
        }
        if (event.getItemDrop().getItemStack().equals(HungerGamer.alivePlayers)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickupItem(PlayerPickupItemEvent event) {
        if (isNotPlaying(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player)
            if (isNotPlaying((Player) event.getEntity()))
                event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (isNotPlaying(event.getPlayer()) && !isAdmin(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if (isNotPlaying(event.getPlayer()) && !isAdmin(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            if (isNotPlaying((Player) event.getEntity())) {
                event.setCancelled(true);
                return;
            }
        }
        if (event.getDamager() instanceof Player) {
            if (isNotPlaying((Player) event.getDamager()) &&
                    !isAdmin((Player) event.getDamager())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player)
            if (isNotPlaying((Player) event.getEntity()))
                event.setCancelled(true);
    }
}
