package com.lostmc.hungergames.kit.registry.fight;

import com.lostmc.bukkit.event.tpall.PlayerTpAllEvent;
import com.lostmc.game.event.combatlog.PlayerDeathInCombatEvent;
import com.lostmc.game.event.combatlog.PlayerDeathOutOfCombatEvent;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.event.kit.GladiatorEndEvent;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GladiatorFight {

    private HungerGames hg;
    private Player gladiator;
    private Player target;
    private Location tpLocGladiator;
    private Location tpLocTarget;
    private BukkitRunnable witherEffect;
    private BukkitRunnable teleportBack;
    private List<Block> blocksToRemove;
    private Listener listener;
    private boolean ended;

    public GladiatorFight(final Player gladiator, final Player target, HungerGames hg) {
        this.hg = hg;
        this.gladiator = gladiator;
        this.target = target;
        this.blocksToRemove = new ArrayList<>();
        send1v1();
        ended = false;
        listener = new Listener() {

            @EventHandler(priority = EventPriority.LOWEST)
            public void onTpAll(PlayerTpAllEvent event) {
                if (isIn1v1(event.getPlayer())) {
                    if (!ended) {
                        ended = true;
                        target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 100000));
                        gladiator.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 100000));
                        teleportBack(tpLocGladiator, tpLocTarget);
                    }
                }
            }

            @EventHandler
            public void onEntityDamage(EntityDamageByEntityEvent e) {
                if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player))
                    return;
                if ((isIn1v1((Player) e.getEntity()) || isIn1v1((Player) e.getDamager()))
                        && (!(isIn1v1((Player) e.getEntity()) && isIn1v1((Player) e.getDamager())))) {
                    e.setCancelled(true);
                }
            }

            @EventHandler(priority = EventPriority.HIGHEST)
            public void onDeath(PlayerDeathEvent e) {
                if (isIn1v1(e.getEntity())) {
                    if (!ended) {
                        ended = true;
                        if (e.getEntity().equals(gladiator)) {
                            // target winner
                            target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 100000));
                            hg.getItemManager().dropItems(e.getEntity(), tpLocGladiator);
                            teleportBack(target, gladiator);
                            return;
                        }
                        // gladiator winner
                        gladiator.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 100000));
                        hg.getItemManager().dropItems(e.getEntity(), tpLocTarget);
                        teleportBack(gladiator, target);
                    }
                }
            }

            @EventHandler(priority = EventPriority.LOWEST)
            public void onMove(PlayerMoveEvent event) {
                Player p = event.getPlayer();
                if (!isIn1v1(p))
                    return;
                if (ended)
                    return;
                if (!blocksToRemove.contains(event.getTo().getBlock())) {
                    ended = true;
                    target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 100000));
                    gladiator.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 100000));
                    teleportBack(tpLocGladiator, tpLocTarget);
                }
            }

            @EventHandler
            public void onQuit(PlayerQuitEvent e) {
                Player p = e.getPlayer();
                if (!isIn1v1(p))
                    return;
                if (e.getPlayer().isDead())
                    return;
                if (!ended) {
                    ended = true;
                    if (p == gladiator) {
                        // target winner
                        target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 100000));
                        hg.getItemManager().dropItems(p, tpLocGladiator);
                        teleportBack(target, gladiator);
                        return;
                    }
                    // gladiator winner
                    gladiator.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 100000));
                    hg.getItemManager().dropItems(p, tpLocTarget);
                    teleportBack(gladiator, target);
                }
            }
        };
        hg.getServer().getPluginManager().registerEvents(listener, hg);
    }

    public boolean isIn1v1(Player player) {
        return player.equals(gladiator) || player.equals(target);
    }

    public void destroy() {
        HandlerList.unregisterAll(listener);
    }

    public void send1v1() {
        Location loc = gladiator.getLocation();
        boolean hasGladi = true;
        while (hasGladi) {
            hasGladi = false;
            boolean stop = false;
            for (double x = -8; x <= 8; x++) {
                for (double z = -8; z <= 8; z++) {
                    for (double y = 0; y <= 10; y++) {
                        Location l = new Location(loc.getWorld(), loc.getX() + x, 120 + y, loc.getZ() + z);
                        if (l.getBlock().getType() != Material.AIR) {
                            hasGladi = true;
                            loc = new Location(loc.getWorld(), loc.getX() + 20, loc.getY(), loc.getZ());
                            stop = true;
                        }
                        if (stop)
                            break;
                    }
                    if (stop)
                        break;
                }
                if (stop)
                    break;
            }
        }
        Block mainBlock = loc.getBlock();
        generateBlocks(mainBlock);
        tpLocGladiator = gladiator.getLocation().clone();
        tpLocTarget = target.getLocation().clone();
        gladiator.sendMessage("§aVocê desafiou " + target.getName() + " para 1v1!");
        target.sendMessage("§aVocê foi desafiado por " + gladiator.getName() + " para 1v1!");
        Location l1 = new Location(mainBlock.getWorld(), mainBlock.getX() + 6.5, 121, mainBlock.getZ() + 6.5);
        l1.setYaw((float) (90.0 * 1.5));
        Location l2 = new Location(mainBlock.getWorld(), mainBlock.getX() - 5.5, 121, mainBlock.getZ() - 5.5);
        l2.setYaw((float) (90.0 * 3.5));
        target.teleport(l1);
        gladiator.teleport(l2);
        hg.getGladiatorFightController().addToFights(this);
        gladiator.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 100000));
        target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 100000));
        witherEffect = new BukkitRunnable() {
            @Override
            public void run() {
                gladiator.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 60, 5));
                target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 60, 5));
            }
        };
        witherEffect.runTaskLater(hg, 20 * 60 * 2);
        teleportBack = new BukkitRunnable() {
            @Override
            public void run() {
                teleportBack(tpLocGladiator, tpLocTarget);
            }
        };
        teleportBack.runTaskLater(hg, 20 * 60 * 3);
    }

    public void teleportBack(Location loc, Location loc1) {
        hg.getGladiatorFightController().removeFromFight(this);
        gladiator.teleport(loc);
        target.teleport(loc1);
        removeBlocks();
        gladiator.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 100000));
        target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 100000));
        gladiator.removePotionEffect(PotionEffectType.WITHER);
        target.removePotionEffect(PotionEffectType.WITHER);
        stop();
        destroy();
        hg.getServer().getPluginManager().callEvent(new GladiatorEndEvent(gladiator, target));
    }

    public void teleportBack(Player winner, Player loser) {
        hg.getGladiatorFightController().removeFromFight(this);
        winner.teleport(tpLocGladiator);
        removeBlocks();
        winner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 100000));
        winner.removePotionEffect(PotionEffectType.WITHER);
        stop();
        destroy();
        hg.getServer().getPluginManager().callEvent(new GladiatorEndEvent(gladiator, target));
    }

    public void generateBlocks(Block mainBlock) {
        for (double x = -8; x <= 8; x++) {
            for (double z = -8; z <= 8; z++) {
                for (double y = 0; y <= 15; y++) {
                    Location l = new Location(mainBlock.getWorld(), mainBlock.getX() + x, 120 + y, mainBlock.getZ() + z);
                    l.getBlock().setType(Material.GLASS);
                    hg.getGladiatorFightController().addBlock(l.getBlock());
                    blocksToRemove.add(l.getBlock());
                }
            }
        }
        for (double x = -7; x <= 7; x++) {
            for (double z = -7; z <= 7; z++) {
                for (double y = 1; y <= 15; y++) {
                    Location l = new Location(mainBlock.getWorld(), mainBlock.getX() + x, 120 + y, mainBlock.getZ() + z);
                    l.getBlock().setType(Material.AIR);
                }
            }
        }
    }

    public void removeBlocks() {
        for (Block b : blocksToRemove) {
            if (b.getType() != null && b.getType() != Material.AIR)
                b.setType(Material.AIR);
            hg.getGladiatorFightController().removeBlock(b);
        }
        blocksToRemove.clear();
    }

    public void stop() {
        witherEffect.cancel();
        teleportBack.cancel();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o instanceof GladiatorFight) {
            GladiatorFight that = (GladiatorFight) o;
            return that.gladiator.equals(this.gladiator)
                    && that.target.equals(this.target);
        }
        return false;
    }
}
