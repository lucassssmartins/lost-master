package com.lostmc.pvp.listener;

import com.lostmc.bukkit.listener.BukkitListener;
import com.lostmc.core.profile.Profile;
import com.lostmc.game.constructor.CombatLog;
import com.lostmc.game.constructor.Gamer;
import com.lostmc.pvp.constructor.PvPGamer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CombatLogListener extends BukkitListener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player e = (Player) event.getEntity();
            PvPGamer gamer = (PvPGamer) Profile.getProfile(e).getResource(Gamer.class);
            CombatLog combatLog = gamer.getCombatLog();
            combatLog.hit((Player) event.getDamager());
        }
    }
}
