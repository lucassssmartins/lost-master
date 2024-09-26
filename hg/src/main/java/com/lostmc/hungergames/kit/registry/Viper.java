package com.lostmc.hungergames.kit.registry;

import com.lostmc.game.GamePlugin;
import com.lostmc.game.event.combatlog.PlayerCombatLogEvent;
import com.lostmc.hungergames.kit.HungerKit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class Viper extends HungerKit {

    private Random random = new Random();
    private PotionEffect potionEffect = new PotionEffect(PotionEffectType.POISON, 100, 1);

    public Viper(GamePlugin plugin) {
        super(plugin);
        setIconMaterial(Material.SPIDER_EYE);
        setBuyPrice(15000);
        setRentPrice(2000);
        setDescription("Envenene outros jogadores com seus ataques.");
    }

    @Override
    public ItemStack getMainItem() {
        return null;
    }

    @EventHandler
    public void onPlayerDamagePlayerListener(PlayerCombatLogEvent e) {
        if (isUsing(e.getCombatPlayer())) {
            if (random.nextInt(2) == 0) {
                e.getPlayer().getLocation().getWorld().playEffect(e.getPlayer().getLocation().add(
                                0.0D, 0.4D, 0.0D), Effect.STEP_SOUND, 18,
                        (short) 1);
                e.getPlayer().addPotionEffect(potionEffect);
            }
        }
    }
}
