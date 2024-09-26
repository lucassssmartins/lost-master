package com.lostmc.pvp.ability.registry;

import com.lostmc.core.profile.Profile;
import com.lostmc.game.GamePlugin;
import com.lostmc.game.constructor.Gamer;
import com.lostmc.game.event.combatlog.PlayerDeathInCombatEvent;
import com.lostmc.pvp.ability.CustomPvPKit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Anchor extends CustomPvPKit {

    public Anchor(GamePlugin plugin) {
        super(plugin);
        setIconMaterial(Material.ANVIL);
        setBuyPrice(15000);
        setRentPrice(1000);
        setIncompatibleKits("Kangaroo");
        setDescription("Não cause e nem receba repulsão por ataque.");
    }

    @Override
    public ItemStack getMainItem() {
        return null;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerDamagePlayerListener(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player p = (Player) e.getEntity();
            Player d = (Player) e.getDamager();
            if (isUsing(p) || isUsing(d)) {
                p.getWorld().playSound(p.getLocation(), Sound.ANVIL_LAND, 0.15F, 1.0F);
                if (((int) p.getHealth() - e.getFinalDamage()) <= 0) {
                    e.setCancelled(true);
                    p.setHealth(p.getMaxHealth());
                    Bukkit.getPluginManager()
                            .callEvent(new PlayerDeathInCombatEvent(p, d, false));
                } else {
                    e.setCancelled(true);
                    Profile.getProfile(p).getResource(Gamer.class).getCombatLog().hit(d);
                    p.damage(e.getFinalDamage());
                }
            }
        }
    }
}
