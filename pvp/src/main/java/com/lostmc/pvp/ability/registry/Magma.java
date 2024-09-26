package com.lostmc.pvp.ability.registry;

import com.lostmc.bukkit.event.timer.ServerTimerEvent;
import com.lostmc.bukkit.vanish.VanishController;
import com.lostmc.game.GamePlugin;
import com.lostmc.game.event.combatlog.PlayerCombatLogEvent;
import com.lostmc.pvp.PvP;
import com.lostmc.pvp.ability.CustomPvPKit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class Magma extends CustomPvPKit {

    private Random random = new Random();

    public Magma(GamePlugin plugin) {
        super(plugin);
        setIconMaterial(Material.LAVA_BUCKET);
        setBuyPrice(15000);
        setRentPrice(2000);
        setDescription("Seja imune ao fogo e faça outros jogadores queimarem.");
    }

    @Override
    public ItemStack getMainItem() {
        return null;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageListener(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.FIRE ||
                e.getCause() == EntityDamageEvent.DamageCause.LAVA ||
                e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK ||
                e.getCause() == EntityDamageEvent.DamageCause.LIGHTNING) {
            if (e.getEntity() instanceof Player) {
                Player p = (Player) e.getEntity();
                if (isUsing(p)) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onUpdateListener(ServerTimerEvent e) {
        if (e.getCurrentTick() % 20 == 0) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (isUsing(p)) {
                    if (!PvP.getControl().getController(VanishController.class).isVanished(p)) {
                        if (p.getLocation().getBlock().getType() == Material.WATER ||
                                p.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
                            p.damage(2.0);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamageListener(PlayerCombatLogEvent e) {
        if (isUsing(e.getCombatPlayer())) {
            if (random.nextInt(9) == 0) {
                e.getPlayer().setFireTicks(50);
            }
        }
    }
}
