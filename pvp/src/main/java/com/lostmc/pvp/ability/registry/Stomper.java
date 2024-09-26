package com.lostmc.pvp.ability.registry;

import com.lostmc.bukkit.vanish.VanishController;
import com.lostmc.core.profile.Profile;
import com.lostmc.game.GamePlugin;
import com.lostmc.game.constructor.Gamer;
import com.lostmc.game.event.combatlog.PlayerDeathInCombatEvent;
import com.lostmc.pvp.PvP;
import com.lostmc.pvp.ability.CustomPvPKit;
import com.lostmc.pvp.constructor.PvPGamer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class Stomper extends CustomPvPKit {

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
                            if (!((PvPGamer) Profile.getProfile(ps).getResource(Gamer.class)).getWarp().isProtected(ps)) {
                                if (!PvP.getControl().getController(VanishController.class).isVanished(ps)) {
                                    if (p.getLocation().distance(ps.getLocation()) <= 5) {
                                        double dmg = e.getDamage();
                                        if ((isSteelhead(ps) || ps.isSneaking()) && dmg > 4)
                                            dmg = 4;
                                        if (((int) ps.getHealth() - dmg) <= 0) {
                                            ps.setHealth(ps.getMaxHealth());
                                            Bukkit.getPluginManager().callEvent(new PlayerDeathInCombatEvent(ps, p, false));
                                            continue;
                                        }
                                        Profile.getProfile(ps).getResource(Gamer.class).getCombatLog().hit(p);
                                        ps.damage(dmg);
                                    }
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
        return Profile.getProfile(p).getResource(Gamer.class).getKits().values().stream().
                filter(ab -> ab.getName().equals("Steelhead")).findFirst().orElse(null) != null;
    }
}
