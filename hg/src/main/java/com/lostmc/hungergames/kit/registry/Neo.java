package com.lostmc.hungergames.kit.registry;

import com.lostmc.game.GamePlugin;
import com.lostmc.hungergames.kit.HungerKit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Neo extends HungerKit {

    public Neo(GamePlugin plugin) {
        super(plugin);
        setIconMaterial(Material.ARROW);
        setBuyPrice(15000);
        setRentPrice(1000);
        setIncompatibleKits("Switcher", "Fisherman");
        setDescription("Seja imune a projétis");
    }

    @Override
    public ItemStack getMainItem() {
        return null;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageListener(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Projectile) {
            if (isUsing((Player) e.getEntity())) {
                e.setCancelled(true);
                if (((Projectile) e.getDamager()).getShooter() instanceof Player) {
                    ((Player) ((Projectile) e.getDamager()).getShooter()).sendMessage("§c"
                            + e.getEntity().getName() + " está usando o kit Neo!");
                }
            }
        }
    }
}
