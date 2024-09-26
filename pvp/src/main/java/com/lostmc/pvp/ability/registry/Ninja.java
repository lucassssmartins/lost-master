package com.lostmc.pvp.ability.registry;

import com.lostmc.core.profile.Profile;
import com.lostmc.game.GamePlugin;
import com.lostmc.game.constructor.Gamer;
import com.lostmc.game.event.combatlog.PlayerCombatLogEvent;
import com.lostmc.game.interfaces.Ejectable;
import com.lostmc.pvp.ability.CustomPvPKit;
import com.lostmc.pvp.ability.registry.fight.GladiatorFight;
import com.lostmc.pvp.constructor.PvPGamer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Ninja extends CustomPvPKit implements Ejectable {

    private HashMap<UUID, NinjaHit> ninjaHits = new HashMap<>();

    public Ninja(GamePlugin plugin) {
        super(plugin);
        setCooldown(5L);
        setIconMaterial(Material.NETHER_STAR);
        setBuyPrice(30000);
        setRentPrice(5000);
        setIncompatibleKits("Gladiator", "Stomper");
        setDescription("Teleporte-se para o último jogador que você atacou.");
    }

    @EventHandler
    public void onCombat(PlayerCombatLogEvent e) {
        if (!isUsing(e.getCombatPlayer()))
            return;
        NinjaHit ninjaHit = ninjaHits.get(e.getCombatPlayer().getUniqueId());
        if (ninjaHit == null) {
            ninjaHit = new NinjaHit(e.getPlayer());
        } else {
            ninjaHit.setTarget(e.getPlayer());
        }
        ninjaHits.put(e.getCombatPlayer().getUniqueId(), ninjaHit);
    }

    @EventHandler
    public void onPlayerToggleSneakListener(PlayerToggleSneakEvent e) {
        if (isUsing(e.getPlayer()) && e.isSneaking()) {
            if (ninjaHits.containsKey(e.getPlayer().getUniqueId())) {
                NinjaHit hit = ninjaHits.get(e.getPlayer().getUniqueId());
                if (hit.getTarget() != null) {
                    if (hit.getTargetExpires() >= System.currentTimeMillis()) {
                        if (hit.getTarget().isOnline()) {
                            if (!hit.getTarget().isDead()) {
                                if (hit.getTarget().getWorld().equals(e.getPlayer().getWorld())) {
                                    if (!((PvPGamer) Profile.getProfile(hit.getTarget()).getResource(Gamer.class)).getWarp().isProtected(hit.getTarget())) {
                                        if (e.getPlayer().getLocation().distance(hit.getTarget().getLocation()) <= 50) {
                                            if (!isInCooldown(e.getPlayer())) {
                                                GladiatorFight fight = getPlugin().getGladiatorFightController().getFight(e.getPlayer());
                                                if (fight == null || fight.isIn1v1(hit.getTarget())) {
                                                    e.getPlayer().teleport(hit.getTarget().getLocation());
                                                    e.getPlayer().sendMessage("§aTeleportado para " + hit.getTarget().getName() + " com sucesso!");
                                                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENDERMAN_TELEPORT, 0.5F, 1.0F);
                                                    putInCooldown(e.getPlayer());
                                                } else {

                                                }
                                            } else {
                                                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.IRONGOLEM_HIT, 0.5F, 1.0F);
                                                sendCooldownMessage(e.getPlayer());
                                            }
                                        } else {

                                        }
                                    } else {

                                    }
                                } else {

                                }
                            } else {

                            }
                        } else {

                        }
                    } else {

                    }
                }
            }
        }
    }


    @Override
    public ItemStack getMainItem() {
        return null;
    }

    @Override
    public void eject(Player player) {
        this.ninjaHits.remove(player.getUniqueId());
    }

    private static class NinjaHit {

        private UUID targetUUID;
        private long targetExpires;

        public NinjaHit(Player target) {
            this.targetUUID = target.getUniqueId();
            this.targetExpires = System.currentTimeMillis() + 15000;
        }

        public Player getTarget() {
            return Bukkit.getPlayer(getTargetUUID());
        }

        public UUID getTargetUUID() {
            return targetUUID;
        }

        public long getTargetExpires() {
            return targetExpires;
        }

        public void setTarget(Player player) {
            this.targetUUID = player.getUniqueId();
            this.targetExpires = System.currentTimeMillis() + 15000;
        }
    }
}
