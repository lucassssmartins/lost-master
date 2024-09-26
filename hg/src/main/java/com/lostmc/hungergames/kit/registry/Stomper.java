package com.lostmc.hungergames.kit.registry;

import com.lostmc.bukkit.vanish.VanishController;
import com.lostmc.core.profile.Profile;
import com.lostmc.game.GamePlugin;
import com.lostmc.game.constructor.Gamer;
import com.lostmc.game.event.combatlog.PlayerDeathInCombatEvent;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.constructor.HungerGamer;
import com.lostmc.hungergames.kit.HungerKit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class Stomper extends HungerKit {

    public Stomper(GamePlugin plugin) {
        super(plugin);
        setIconMaterial(Material.IRON_BOOTS);
        setBuyPrice(40000);
        setRentPrice(8000);
        setIncompatibleKits("Kangaroo", "Neo", "Ninja");
        setDescription("Transmita seu dano de queda para jogadores próximos.");
    }

    @Override
    public ItemStack getMainItem() {
        return null;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamageListener(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (e.getEntity() instanceof Player) {
                Player p = (Player) e.getEntity();
                if (isUsing(p)) {
                    p.getWorld().playSound(p.getLocation(), Sound.ANVIL_LAND, 0.15F, 1.0F);
                    for (Player ps : Bukkit.getOnlinePlayers()) {
                        if (ps.getUniqueId() != p.getUniqueId()) {
                            if (!HungerGamer.getGamer(ps).isNotPlaying() && !HungerGames.getControl().getController(VanishController.class).isVanished(ps)) {
                                if (p.getLocation().distance(ps.getLocation()) <= 5) {
                                    double dmg = e.getDamage();
                                    if ((isSteelhead(ps) || ps.isSneaking()))
                                        dmg = 0;
                                    ps.damage(dmg, p);
                                }
                            }
                        }
                    }
                    if (e.getDamage() > 4) {
                        e.setDamage(4);
                    }
                }
            }
        }
    }

    public boolean isSteelhead(Player p) {
        return HungerGamer.getGamer(p).getKits().values().stream().
                filter(ab -> ab.getName().equals("Steelhead")).findFirst().orElse(null) != null;
    }
}
