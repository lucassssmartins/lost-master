package com.lostmc.pvp.listener;

import com.lostmc.bukkit.listener.BukkitListener;
import com.lostmc.bukkit.vanish.VanishController;
import com.lostmc.core.profile.Profile;
import com.lostmc.game.constructor.Gamer;
import com.lostmc.pvp.PvP;
import com.lostmc.pvp.constructor.PvPGamer;
import com.lostmc.pvp.warp.Warp;
import com.lostmc.pvp.warp.registry.FPSWarp;
import com.lostmc.pvp.warp.registry.FightWarp;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemListener extends BukkitListener {

    private List<String> materialNames = new ArrayList<>();

    public ItemListener() {
        materialNames.add("MUSHROOM_SOUP");
        materialNames.add("BROWN_MUSHROOM");
        materialNames.add("RED_MUSHROOM");
        materialNames.add("BOWL");
        materialNames.add("INK_SACK");
        materialNames.add("COCOA");
        materialNames.add("LEATHER");
        materialNames.add("_BOOTS");
        materialNames.add("_LEGGINGS");
        materialNames.add("_CHESTPLATE");
        materialNames.add("_HELMET");
        materialNames.add("POTION");
        materialNames.add("EXP_BOTTLE");
        materialNames.add("CACTUS");
    }

    @EventHandler
    public void onDropItemListener(PlayerDropItemEvent event) {
        Warp warp = ((PvPGamer) Profile.getProfile(event.getPlayer()).getResource(Gamer.class)).getWarp();
        if (!(warp instanceof FightWarp) && !(warp instanceof FPSWarp)) {
            Item drop = event.getItemDrop();
            Iterator<String> it = materialNames.iterator();
            while (it.hasNext()) {
                String typeName = drop.getItemStack().getType().toString();
                String next = it.next();
                if (typeName.equals(next) || typeName.contains(next)) {
                    return;
                }
            }
            event.setCancelled(true);
        } else if (warp instanceof FPSWarp || ((FightWarp) warp).isInFight(event.getPlayer())) {
            Material mat = event.getItemDrop().getItemStack().getType();
            if (mat != Material.MUSHROOM_SOUP && mat != Material.BOWL && mat != Material.INK_SACK &&
                    mat != Material.COCOA && mat != Material.RED_MUSHROOM && mat != Material.BROWN_MUSHROOM
                    && !mat.toString().contains("_BOOTS") && !mat.toString().contains("_LEGGINGS")
                    && !mat.toString().contains("_CHESTPLATE") && !mat.toString().contains("_HELMET"))
                event.setCancelled(true);
            else {
                event.getItemDrop().remove();
            }
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItemListener(PlayerPickupItemEvent event) {
        if (PvP.getControl().getController(VanishController.class).isVanished(event.getPlayer())) {
            event.setCancelled(true);
        } else {
            Warp warp = ((PvPGamer) Profile.getProfile(event.getPlayer()).getResource(Gamer.class)).getWarp();
            if (!(warp instanceof FightWarp)) {
                Iterator<String> it = materialNames.iterator();
                while (it.hasNext()) {
                    String typeName = event.getItem().getItemStack().getType().toString();
                    String next = it.next();
                    if (typeName.equals(next) || typeName.contains(next)) {
                        return;
                    }
                }
            } else if (((FightWarp) warp).isInFight(event.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }
}
