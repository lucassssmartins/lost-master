package com.lostmc.hungergames.listener;

import com.lostmc.game.constructor.CombatLog;
import com.lostmc.game.event.combatlog.PlayerDeathInCombatEvent;
import com.lostmc.hungergames.constructor.HungerListener;
import com.lostmc.hungergames.constructor.HungerGamer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CombatLogListener extends HungerListener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player e = (Player) event.getEntity();
            HungerGamer gamer = HungerGamer.getGamer(e);
            CombatLog combatLog = gamer.getCombatLog();
            combatLog.hit((Player) event.getDamager());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        Player p = event.getEntity();
        HungerGamer.getGamer(p).getCombatLog().logout();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        HungerGamer gamer = HungerGamer.getGamer(p);
        CombatLog combatLog = gamer.getCombatLog();
        if (combatLog.isLogged()) {
            Player combatLogger = Bukkit.getPlayer(combatLog.getCombatLogged());
            if (combatLogger != null)
                if (combatLogger.isOnline())
                    p.damage(p.getMaxHealth());
        }
    }

    @EventHandler
    public void onVoidDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        if (event.getCause() != EntityDamageEvent.DamageCause.VOID)
            return;
        Player p = (Player) event.getEntity();
        HungerGamer gamer = HungerGamer.getGamer(p);
        CombatLog log = gamer.getCombatLog();
        if (log.isLogged()) {
            Player combatLogger = Bukkit.getPlayer(log.getCombatLogged());
            if (combatLogger != null)
                if (combatLogger.isOnline())
                    p.damage(p.getMaxHealth(), combatLogger);
        }
    }
}
