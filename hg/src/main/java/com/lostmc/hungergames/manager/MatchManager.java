package com.lostmc.hungergames.manager;

import com.lostmc.game.GamePlugin;
import com.lostmc.game.manager.Management;
import com.lostmc.hungergames.constructor.HungerGamer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

@Getter
@Setter
public class MatchManager extends Management implements Listener {

    private boolean buildEnabled = true;
    private boolean damageEnabled = true;
    private boolean pvpEnabled = true;

    public MatchManager(GamePlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        registerListener(this);
    }

    public boolean isAlive(Player p) {
        return HungerGamer.getGamer(p).isAlive();
    }

    //@EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!isBuildEnabled() && isAlive(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    //@EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!isBuildEnabled() && isAlive(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (!isDamageEnabled() && isAlive((Player) event.getEntity())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            if (!isPvpEnabled() && isAlive((Player) event.getEntity())) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void onDisable() {

    }
}
