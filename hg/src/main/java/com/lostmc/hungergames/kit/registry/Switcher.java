package com.lostmc.hungergames.kit.registry;

import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.core.profile.Profile;
import com.lostmc.game.GamePlugin;
import com.lostmc.game.constructor.Gamer;
import com.lostmc.hungergames.constructor.HungerGamer;
import com.lostmc.hungergames.kit.HungerKit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Switcher extends HungerKit {

    public Switcher(GamePlugin plugin) {
        super(plugin);
        setCooldown(2L);
        setIconMaterial(Material.SNOW_BALL);
        setItems(new ItemBuilder(Material.SNOW_BALL).setName("§aSwitcher").build());
        setBuyPrice(20000);
        setRentPrice(2000);
        setIncompatibleKits("Neo");
        setDescription("Troque de lugar com outros jogadores usando sua bola de neve.");
    }

    @Override
    public ItemStack getMainItem() {
        return getItems()[0];
    }

    @EventHandler
    public void onInteractListener(PlayerInteractEvent e) {
        if (isUsing(e.getPlayer())) {
            if (isMainItem(e.getItem())) {
                e.setCancelled(true);
                if (!isInCooldown(e.getPlayer())) {
                    putInCooldown(e.getPlayer());
                    e.getPlayer().launchProjectile(Snowball.class);
                } else
                    sendCooldownMessage(e.getPlayer());
                e.getPlayer().updateInventory();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntityListener(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e.getDamager() instanceof Snowball) {
                Snowball ball = (Snowball) e.getDamager();
                if (ball.getShooter() instanceof Player) {
                    Player shooter = (Player) ball.getShooter();
                    if (isUsing(shooter)) {
                        Player hit = (Player) e.getEntity();
                        if (!isNeo(hit)) {
                            Location loc = shooter.getLocation();
                            shooter.teleport(hit.getLocation());
                            hit.teleport(loc);
                        }
                    }
                }
            }
        }
    }

    public boolean isNeo(Player p) {
        return HungerGamer.getGamer(p).getKits().values().stream().
                filter(ab -> ab.getName().equals("Neo")).findFirst().orElse(null) != null;
    }
}
