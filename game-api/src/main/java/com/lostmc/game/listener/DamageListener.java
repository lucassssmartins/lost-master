package com.lostmc.game.listener;

import com.lostmc.bukkit.listener.BukkitListener;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DamageListener extends BukkitListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageEvent(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player))
            return;
        Player p = (Player) event.getDamager();
        ItemStack sword = p.getItemInHand();
        double damage = event.getDamage();
        double danoEspada = getDamage(sword.getType());
        boolean isMore = false;
        if (damage > 1) {
            isMore = true;
        }
        if (p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
            for (PotionEffect effect : p.getActivePotionEffects()) {
                if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                    double minus;
                    if (isCrital(p)) {
                        minus = (danoEspada + (danoEspada / 2)) * 1.3 * (effect.getAmplifier() + 1);
                    } else {
                        minus = danoEspada * 1.3 * (effect.getAmplifier() + 1);
                    }
                    damage = damage - minus;
                    damage += 2 * (effect.getAmplifier() + 1);
                    break;
                }
            }
        }
        if (!sword.getEnchantments().isEmpty()) {
            if (sword.containsEnchantment(Enchantment.DAMAGE_ARTHROPODS) && isArthropod(event.getEntityType())) {
                damage = damage - (1.5 * sword.getEnchantmentLevel(Enchantment.DAMAGE_ARTHROPODS));
                damage += 1 * sword.getEnchantmentLevel(Enchantment.DAMAGE_ARTHROPODS);
            }
            if (sword.containsEnchantment(Enchantment.DAMAGE_UNDEAD) && isUndead(event.getEntityType())) {
                damage = damage - (1.5 * sword.getEnchantmentLevel(Enchantment.DAMAGE_UNDEAD));
                damage += 1 * sword.getEnchantmentLevel(Enchantment.DAMAGE_UNDEAD);
            }
            if (sword.containsEnchantment(Enchantment.DAMAGE_ALL)) {
                damage = damage - 1.25 * sword.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
                damage += 1 * sword.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
            }
        }
        if (isCrital(p)) {
            damage = damage - (danoEspada / 2);
            damage += 1;
        }
        if (isMore)
            damage -= 2;
        event.setDamage(damage);
    }

    @SuppressWarnings("deprecation")
    private boolean isCrital(Player p) {
        return p.getFallDistance() > 0 && !p.isOnGround() && !p.hasPotionEffect(PotionEffectType.BLINDNESS);
    }

    private boolean isArthropod(EntityType type) {
        switch (type) {
            case CAVE_SPIDER:
                return true;
            case SPIDER:
                return true;
            case SILVERFISH:
                return true;
            default:
                break;
        }
        return false;
    }

    private boolean isUndead(EntityType type) {
        switch (type) {
            case SKELETON:
                return true;
            case ZOMBIE:
                return true;
            case WITHER_SKULL:
                return true;
            case PIG_ZOMBIE:
                return true;
            default:
                break;
        }
        return false;
    }

    private double getDamage(Material type) {
        double damage = 1.0;
        if (type.toString().contains("DIAMOND_")) {
            damage = 8.0;
        } else if (type.toString().contains("IRON_")) {
            damage = 7.0;
        } else if (type.toString().contains("STONE_")) {
            damage = 5.0;
        } else if (type.toString().contains("WOOD_")) {
            damage = 4.0;
        } else if (type.toString().contains("GOLD_")) {
            damage = 4.0;
        }
        if (!type.toString().contains("_SWORD")) {
            damage--;
            if (!type.toString().contains("_AXE")) {
                damage--;
                if (!type.toString().contains("_PICKAXE")) {
                    damage--;
                    if (!type.toString().contains("_SPADE")) {
                        damage = 1.0;
                    }
                }
            }
        }
        return damage;
    }
}