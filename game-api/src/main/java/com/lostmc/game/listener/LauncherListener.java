package com.lostmc.game.listener;

import com.lostmc.bukkit.listener.BukkitListener;
import com.lostmc.core.Commons;
import com.lostmc.core.server.ServerType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LauncherListener extends BukkitListener {

    private Set<UUID> noFallDamage;

    public LauncherListener() {
        noFallDamage = new HashSet<>();
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if (!p.isOnGround())
            return;
        Location standBlock = p.getLocation().clone().add(0, -1, 0);
        if (standBlock.getBlock().getType() == Material.SPONGE) {
            double xvel = 0.0D;
            double yvel = 3.5D;
            double zvel = 0.0D;
            p.setVelocity(new Vector(xvel, yvel, zvel));
            p.playSound(p.getLocation(), Sound.HORSE_JUMP, 10.0f, 1.0f);
            if (!noFallDamage.contains(p.getUniqueId())) {
                noFallDamage.add(p.getUniqueId());
            }
        } else if (standBlock.getBlock().getType() == Material.EMERALD_BLOCK) {
            Vector vector = p.getLocation().getDirection();
            if (Commons.getProxyHandler().getLocal().getServerType() == ServerType.HG_LOBBY)
                vector.multiply(3.91F).setY(0.5F);
            else
                vector.multiply(1.51F).setY(0.5F);
            p.setVelocity(vector);
            p.playSound(p.getLocation(), Sound.HORSE_JUMP, 10.0f, 1.0f);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageEvent event) {
        if (event.getCause() != DamageCause.FALL)
            return;
        if (!(event.getEntity() instanceof Player))
            return;
        Player p = (Player) event.getEntity();
        if (noFallDamage.contains(p.getUniqueId())) {
            event.setCancelled(true);
            noFallDamage.remove(p.getUniqueId());
        }
    }
}
