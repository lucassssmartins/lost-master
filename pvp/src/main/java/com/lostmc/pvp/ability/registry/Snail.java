package com.lostmc.pvp.ability.registry;

import com.lostmc.game.GamePlugin;
import com.lostmc.game.event.combatlog.PlayerCombatLogEvent;
import com.lostmc.pvp.ability.CustomPvPKit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class Snail extends CustomPvPKit {

    private Random random = new Random();
    private PotionEffect potionEffect = new PotionEffect(PotionEffectType.SLOW, 5 * 20, 2);

    public Snail(GamePlugin plugin) {
        super(plugin);
        setIconMaterial(Material.WEB);
        setBuyPrice(15000);
        setRentPrice(2000);
        setDescription("Cause lentidão nos jogadores com seus ataques.");
    }

    @Override
    public ItemStack getMainItem() {
        return null;
    }

    @EventHandler
    public void onPlayerDamagePlayerListener(PlayerCombatLogEvent e) {
        if (isUsing(e.getCombatPlayer())) {
            if (random.nextInt(2) == 0) {
                e.getPlayer().addPotionEffect(potionEffect);
                e.getPlayer().getWorld().playEffect(e.getPlayer().getLocation().add(
                        0D, 0.4D, 0D), Effect.STEP_SOUND, 159, (short) 13);
            }
        }
    }
}
