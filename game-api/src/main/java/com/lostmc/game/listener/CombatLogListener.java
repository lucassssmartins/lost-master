package com.lostmc.game.listener;

import com.lostmc.bukkit.listener.BukkitListener;
import com.lostmc.core.profile.Profile;
import com.lostmc.game.GamePlugin;
import com.lostmc.game.GameType;
import com.lostmc.game.constructor.CombatLog;
import com.lostmc.game.constructor.Gamer;
import com.lostmc.game.event.combatlog.PlayerDeathInCombatEvent;
import com.lostmc.game.event.combatlog.PlayerDeathOutOfCombatEvent;
import com.lostmc.game.event.player.PlayerPreDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CombatLogListener extends BukkitListener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void damage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();

            if (((int) p.getHealth() - event.getFinalDamage()) > 0) {
                return;
            }

            Profile profile = Profile.getProfile(p);
            Gamer gamer = profile.getResource(Gamer.class);

            if (gamer != null) {
                CombatLog combatLog = gamer.getCombatLog();

                Player combatPlayer = null;
                if (combatLog.isLogged()) {
                    combatPlayer = Bukkit.getPlayer(combatLog.getCombatLogged());
                }

                PlayerPreDeathEvent deathEvent = new PlayerPreDeathEvent(p, combatPlayer, event.getCause());
                Bukkit.getPluginManager().callEvent(deathEvent);

                if (((GamePlugin) getPlugin()).getGameType() == GameType.PVP || deathEvent.isCancelled()) {
                    event.setCancelled(true);
                    p.setHealth(p.getMaxHealth());
                    if (combatPlayer != null) {
                        Bukkit.getPluginManager().callEvent(new PlayerDeathInCombatEvent(p, combatPlayer, false));
                    } else {
                        Bukkit.getPluginManager().callEvent(new PlayerDeathOutOfCombatEvent(p));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void damageEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();

            if (((int) p.getHealth() - event.getFinalDamage()) > 0) {
                return;
            }

            Entity killer = event.getDamager();
            if (event.getDamager() instanceof Projectile) {
                if (((Projectile) event.getDamager()).getShooter() instanceof Entity) {
                    killer = (Entity) ((Projectile) event.getDamager()).getShooter();
                }
            }

            PlayerPreDeathEvent deathEvent = new PlayerPreDeathEvent(p, killer, event.getCause());
            Bukkit.getPluginManager().callEvent(deathEvent);

            if (((GamePlugin) getPlugin()).getGameType() == GameType.PVP || deathEvent.isCancelled()) {
                event.setCancelled(true);
                p.setHealth(p.getMaxHealth());
                if (killer instanceof Player) {
                    Bukkit.getPluginManager().callEvent(new PlayerDeathInCombatEvent(p, (Player) killer, false));
                } else {
                    Bukkit.getPluginManager().callEvent(new PlayerDeathOutOfCombatEvent(p));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void quit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getProfile(player);
        Gamer gamer = profile.getResource(Gamer.class);

        if (gamer != null) {
            CombatLog combatLog = gamer.getCombatLog();
            if (!combatLog.isLogged()) {
                return;
            }

            Player combatLogged = Bukkit.getPlayer(combatLog.getCombatLogged());
            if (combatLogged == null) {
                return;
            }

            Bukkit.getPluginManager().callEvent(new PlayerDeathInCombatEvent(player, combatLogged, true));
        }
    }
}
