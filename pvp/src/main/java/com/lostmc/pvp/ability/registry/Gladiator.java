package com.lostmc.pvp.ability.registry;

import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.core.profile.Profile;
import com.lostmc.game.GamePlugin;
import com.lostmc.game.constructor.Gamer;
import com.lostmc.pvp.ability.CustomPvPKit;
import com.lostmc.pvp.ability.registry.event.GladiatorEndEvent;
import com.lostmc.pvp.ability.registry.fight.GladiatorFight;
import com.lostmc.pvp.constructor.PvPGamer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

public class Gladiator extends CustomPvPKit {

    public Gladiator(GamePlugin plugin) {
        super(plugin);
        setCooldown(5L);
        setIconMaterial(Material.IRON_FENCE);
        setItems(new ItemBuilder(Material.IRON_FENCE).setName("§aGladiator").build());
        setBuyPrice(40000);
        setRentPrice(6000);
        setIncompatibleKits("Ninja");
        setDescription("Desafie um jogador para um duelo 1v1 em uma arena nos céus.");
    }

    @Override
    public ItemStack getMainItem() {
        return getItems()[0];
    }

    @EventHandler
    public void end(GladiatorEndEvent event) {
        putInCooldown(event.getGladiator());
    }

    @EventHandler
    public void onPlayerInteractEntityListener(PlayerInteractEntityEvent e) {
        if (e.getPlayer().getItemInHand() != null) {
            if (isMainItem(e.getPlayer().getItemInHand())) {
                e.setCancelled(true);
                if (isUsing(e.getPlayer())) {
                    if (e.getRightClicked() instanceof Player) {
                        Player t = (Player) e.getRightClicked();
                        if (!isNeo(t)) {
                            if (!((PvPGamer) Profile.getProfile(t).getResource(Gamer.class)).getWarp().isProtected(t)) {
                                if (!getPlugin().getGladiatorFightController().isInFight(e.getPlayer())) {
                                    if (!getPlugin().getGladiatorFightController().isInFight(t)) {
                                        new GladiatorFight(e.getPlayer(), t, getPlugin());
                                    } else {
                                        e.getPlayer().sendMessage("§cEste jogador já está em batalha!");
                                    }
                                } else {
                                    e.getPlayer().sendMessage("§cVocê já está em uma batalha!");
                                }
                            } else {
                                e.getPlayer().sendMessage("§cVocê não pode puxar este jogador!");
                            }
                        } else {
                            e.getPlayer().sendMessage("§cO jogador " + t.getName() + " está com o kit Neo!");
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onBlock(BlockDamageEvent event) {
        if (getPlugin().getGladiatorFightController().isFightBlock(event.getBlock())) {
            final Block b = event.getBlock();
            final Player p = event.getPlayer();
            p.sendBlockChange(b.getLocation(), Material.BEDROCK, (byte) 0);
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        Iterator<Block> blockIt = event.blockList().iterator();
        while (blockIt.hasNext()) {
            Block b = blockIt.next();
            if (getPlugin().getGladiatorFightController().isFightBlock(b)) {
                blockIt.remove();
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (getPlugin().getGladiatorFightController().isFightBlock(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    public boolean isNeo(Player p) {
        return Profile.getProfile(p).getResource(Gamer.class).getKits().values().stream().
                filter(ab -> ab.getName().equals("Neo")).findFirst().orElse(null) != null;
    }
}
