package com.lostmc.hungergames.kit.registry;

import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.game.GamePlugin;
import com.lostmc.game.event.combatlog.PlayerCombatLogEvent;
import com.lostmc.game.interfaces.Ejectable;
import com.lostmc.hungergames.kit.HungerKit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Kangaroo extends HungerKit implements Ejectable {

    private static List<UUID> kangarooUses = new ArrayList<>();

    public Kangaroo(GamePlugin plugin) {
        super(plugin);
        setCooldown(5L);
        setIconMaterial(Material.FIREWORK);
        setItems(new ItemBuilder(Material.FIREWORK).setName("§aKangaroo").build());
        setBuyPrice(30000);
        setRentPrice(5000);
        setIncompatibleKits("Stomper", "Anchor");
        setDescription("Mova-se rapidamente pelo mapa com seu foguete.");
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK
                || event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (isUsing(player) && isMainItem(event.getItem())) {
                event.setCancelled(true);

                if (kangarooUses.contains(player.getUniqueId()))
                    return;

                if (isInCooldown(player)) {
                    sendCooldownMessage(player);
                    return;
                }

                Vector vector = player.getEyeLocation().getDirection();
                if (player.isSneaking()) {
                    vector = vector.multiply(1.91F).setY(0.5F);
                } else {
                    vector = vector.multiply(0.5F).setY(1F);
                }

                player.setFallDistance(-1.0F);
                player.setVelocity(vector);
                kangarooUses.add(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player))
            return;
        if (!isUsing((Player) e.getEntity()))
            return;
        if (e.getCause() != EntityDamageEvent.DamageCause.FALL)
            return;
        if (e.getDamage() > 7.0D) {
            e.setDamage(5.0D);
        } else if (e.getDamage() < 2.0D) {
            e.setCancelled(true);
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (kangarooUses.contains(e.getPlayer().getUniqueId())
                && (e.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR
                || e.getPlayer().isOnGround())) {
            kangarooUses.remove(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onCombat(PlayerCombatLogEvent event) {
        Player player = event.getPlayer();
        if (!isUsing(player))
            return;
        putInCooldown(player);
    }

    @Override
    public ItemStack getMainItem() {
        return getItems()[0];
    }

    @Override
    public void eject(Player player) {
        kangarooUses.remove(player);
    }
}
